 #include <thread>
 using namespace std;

 int X, Y, a, b;

 void thread1() {
     X = 1;
     a = Y;
 }

 void thread2() {
     Y = 1;
     b = X;
 }

 int main () {
     int cnt = 0;
     
     do {
         X = 0; Y = 0;

         thread first(thread1);
         thread second(thread2);
      
         first.join();
         second.join();
         cnt++;
        
     } while (a != 0 || b != 0);

     printf("%d",X);
     return 0;
 }

