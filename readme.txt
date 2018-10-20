To run the program, you can just use run.sh e.g.

    ./run.sh <input filename>

Or without the script, just do:

    javac -d bin hitcount/HitCount.java

to compile and

    java -cp ./bin hitcount.HitCount <input filename>

to run

All of this assumes java 8 is already installed on the system.


foo.txt is a basic testcase

Thoughts on the runtime complexity are in comments in hitcount/HitCount.java
