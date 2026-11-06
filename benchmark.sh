#!/bin/bash


#arg1 softwares full path
#arg2 problems full path
#arg3 results dir full path
#arg4 gen2sat calculi dir
function init {
	echo init
	softwares_path=$1
	gen2sat_jar_path=$softwares_path/gen2sat/gen2sat.jar
	mettel_dir_path=$softwares_path/mettel
	kems_jar_path=$softwares_path/KEMS/kems.export/kems.jar
	problems_dir=$2
	results_dir=$3
	calculi_dir=$4
	mettel_timeout=10000
	current_dir=`pwd`
	mkdir -p $results_dir
	rm -rf $results_dir/*

	
}

function clean {
	echo clean
	rm -rf $problems_dir/mettel
	rm -rf $problems_dir/gen2sat
}

function fetch_problems {
	./translateKemsToMettelAndGensat.sh $problems_dir $calculi_dir $softwares_path
	./generate_kems_seq_file.sh $problems_dir/KEMS
	chmod -R 755 $problems_dir
}


#arg1 logic/calculus name (prefix of calculus file)
function benchmark_gen2sat {
	echo benchmark_gen2sat
	local calculus=$1
	local Detailed=Detailed
	local Undetailed=Undetailed
	local detailed_input=$problems_dir/gen2sat/$calculus$Detailed.txt
	local undetailed_input=$problems_dir/gen2sat/$calculus$Undetailed.txt
	local detailed_output=$results_dir/gen2sat$calculus$Detailed.txt
	local undetailed_output=$results_dir/gen2sat$calculus$Undetailed.txt
	echo detailed: $detailed_input
	echo undetailed: $undetailed_input
	java -jar $gen2sat_jar_path $detailed_input >& $detailed_output
	java -jar $gen2sat_jar_path $undetailed_input >& $undetailed_output
	beutify_gen2sat $detailed_output $undetailed_output
}

function benchmark_kems {
	echo benchmark_kems
	java -jar $kems_jar_path $problems_dir/KEMS/sequence.seq >& $results_dir/kems.txt 
	beutify_kems
}


#arg1 family full path 
#arg2 calculus mettel name e.g. C1, Lukasiewicz or Lukavron
#arg3 output path
function benchmark_mettel_one_family {
	clean
	fetch_problems
	echo benchmark_mettel_one_family 
	local family_path=$1
	local calculus=$2
	cd $mettel_dir_path
	local Benchmark=Benchmark
	local result_file=$3
	java -cp $calculus.jar:mettel2-util.jar:opencsv.jar $calculus.tableau.$calculus.$calculus$Benchmark -t $mettel_timeout -d $family_path >> $results_dir/$result_file
	cd $current_dir
}

#arg1: calculus name in mettel
function benchmark_mettel {
	echo benchmark_mettel $1
	local calculus=$1
	local result_file=mettel$calculus.txt
	for d in `find $problems_dir/mettel/ -type d | grep -i c1 | grep -v '\._'`;
	do
		benchmark_mettel_one_family $d $calculus $result_file
	done
	beutify_mettel $calculus $result_file
}

#arg1 calculus name
#arg2 outputpath
function beutify_mettel {
	local calculus=$1
	local result_file=$2
	echo beutify_mettel $1 $2
	local current_dir=`pwd`
	local tmp_file=mettel.tmp
	cd $results_dir
	rm $tmp_file
	cat $result_file | grep "time\|complete" | grep -v '\._'  |cut -d] -f2 | cut -d' ' -f1,2,3,5| sed "s/,//g" |sed "s/^/mettel-$calculus;/g"| sed 's/mtl/prove/'| sed 's/completed//g'|tr ' ' ';' | sed 's/time-out/ /' | sed 's/;;/;/g' | sed 's/Unsatisfiable/provable/g' | sed 's/Satisfiable/unprovable/g' >> $tmp_file
	cp $tmp_file $result_file
	rm $tmp_file
	cd $current_dir
}

#arg1 detailed path
#arg2 undetailed path
function beutify_gen2sat {
	echo beutify_gen2sat
	local detailed_output=$1
	local undetailed_output=$2
	local current_dir=`pwd`
	local tmp_file=gen2sat.tmp
	cd $results_dir
	rm $tmp_file
	cat $detailed_output | grep provable | sed 's/mtl/prove/'| sed 's/^/gen2sat_detailed;/g' >> $tmp_file
	cp $tmp_file $detailed_output
	rm $tmp_file

	cat $undetailed_output | grep provable | sed 's/mtl/prove/'|sed 's/^/gen2sat_undetailed;/g' >> $tmp_file
	cp $tmp_file $undetailed_output
	rm $tmp_file
	cd $current_dir	
}

function beutify_kems {
	echo beutify_kems
	current_dir=`pwd`
	tmp_file=kems.tmp
	cd $results_dir
	rm $tmp_file
	cat kems.txt | cut -d ';' -f 1,5,6 | grep "prove" | grep -v log4j | sed 's/^/kems;/g' | sed 's:; n/a ; n/a:; :' | sed 's/closed/provable/g' | sed 's/open/unprovable/g' >> $tmp_file
	cp $tmp_file kems.txt
	rm $tmp_file
	cd $current_dir

}

#arg1 file to trim in
function trim {
	echo trim $1
	file=$1
	tmp_file=$file.tmp
	rm $tmp_file
	cat $file | sed "s/ //g" > $tmp_file
	cp $tmp_file $file
	rm $tmp_file

}

function trim_all {
	echo trim_all
	current_dir=`pwd`
	cd $results_dir
	for f in `ls`;
	do
		trim $f
	done
	cd $current_dir
}

#arg1 file to replace in
function replace_semicolon_with_tab {
	echo replace_semicolon_with_tab $1
	file=$1
	tmp_file=$file.tmp
	rm $tmp_file
	cat $file | tr ';' '\t' > $tmp_file
	cp $tmp_file $file
	rm $tmp_file


}

function replace_semicolon_with_tab_all {
	echo replace_semicolon_with_tab_all
	current_dir=`pwd`
	cd $results_dir
	for f in `ls`;
	do
		replace_semicolon_with_tab $f
	done
	cd $current_dir
}




function beutify {
	echo beutify
	trim_all
	replace_semicolon_with_tab_all
}

echo in benchmarks. java version
which java
init `realpath $1` `realpath $2` `realpath $3` `realpath $4`
clean
fetch_problems
benchmark_gen2sat C1
benchmark_gen2sat Lukavron
benchmark_mettel C1
benchmark_mettel Lukasiewicz
benchmark_mettel Lukavron
#benchmark_kems C1
beutify 
