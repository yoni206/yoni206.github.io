 #include <thread>
 #include <unistd.h>

 int x;
 void f1() { usleep(1000); x = 1; }
 void f2() { x = 2;  usleep(1000); x = x + 2; }

 int main () {
     while (x != 3) {
       std::thread t1(f1); std::thread t2(f2); 
       t1.join(); t2.join(); 
       printf("%d\n",x);
     }
     return 0;
 }

