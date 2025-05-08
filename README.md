# SHA256_clone_v2

This package is my second attempt at making a SHA256 clone. SHA stands for Secure Hashing Algorithm, and 256 is 
the number of bits in the output hash. 

How secure is 256 bit security? Check out this awesome video by 
[3Blue1Brown on youtube](https://www.youtube.com/watch?v=S9JGmA5_unY&ab_channel=3Blue1Brown3Blue1Brown). 
Hint: very secure!

This wouldn't have been possible without [this video](https://www.youtube.com/watch?v=f9EbD6iY9zI) by learnmeabitcoin.

## How to run:

1) Download the HashFunction.java file and place it into a folder named "sha256"

2) Open Terminal and navigate to the directory where your "sha256" folder is located

3) Run the following terminal command to compile the program: 

`javac sha256/HashFunction.java`

4) The program is now compiled and ready to use!


5) Run the following terminal command replacing "abc" with whatever text you want to hash:

`java sha256.HashFunction abc`

6) The Hash of your input string will be printed to terminal!

You can test the program by running the command with the input string "abc" which will print:


- ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad

Recreating this algorithm has been a fun exercise. I find cryptography in general very interesting, especially 
how a series of relatively simple operations can produce a hash value virtually impossible to reverse. 

Feel free to follow me or star the project. Thanks for checking this out! 
