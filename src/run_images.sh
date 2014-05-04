#!/bin/bash
javac *.java
alias program='java MediaQuery'
echo "Running Search Image Tests\n" > results.txt

echo "-----------------------\n\n" >> results.txt
echo "Running Starbucks Logo Level 0" >> results.txt
program ../Images/starbucks_source_img.v576.rgb ../Images/starbucks_lev0.v576.rgb
read VALUE

echo "-----------------------\n\n" >> results.txt
echo "Running Starbucks Logo Level 1" >> results.txt
program ../Images/starbucks_source_img.v576.rgb ../Images/starbucks_lev1-1.v576.rgb
read VALUE

echo "-----------------------\n\n" >> results.txt
echo "Running Starbucks Logo Level 2" >> results.txt
program ../Images/starbucks_source_img.v576.rgb ../Images/starbucks_lev2-1.v576.rgb
read VALUE

echo "-----------------------\n\n" >> results.txt
echo "Running Oranges Logo Level 0" >> results.txt
program ../Images/orange_source_img.v576.rgb ../Images/orange_lev0.v576.rgb
read VALUE

echo "-----------------------\n\n" >> results.txt
echo "Running Oranges Logo Level 1" >> results.txt
program ../Images/orange_source_img.v576.rgb ../Images/orange_lev1-1.v576.rgb
read VALUE

echo "-----------------------\n\n" >> results.txt
echo "Running Oranges Logo Level 2" >> results.txt
program ../Images/orange_source_img.v576.rgb ../Images/orange_lev2-1.v576.rgb
read VALUE