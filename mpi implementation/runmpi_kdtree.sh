#!/bin/sh

# define an array of file names
files="kd_tree_points-10000-20231203174103.txt 
       kd_tree_points-55000-20231203181110.txt 
       kd_tree_points-100000-20231203174145.txt 
       kd_tree_points-550000-20231203181102.txt 
       kd_tree_points-1000000-20231203174154.txt"

# iterate through each file and number of processes
for file in $files; do
    for np in 1 2 3 4; do
        # extract the number of points from the file name
        points=$(echo $file | grep -o -E '[0-9]+')
        # run the script and redirect output to the corresponding file
        ./run_kdtreempi.sh $np $file 10:50 10:35 > "outmpi_${np}_${points}.txt"
    done
done