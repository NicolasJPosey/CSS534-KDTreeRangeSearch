# KD-Tree MPI Implementation in Java

## Overview
This repository contains a Java-based implementation of a KD-Tree, optimized for efficient range querying of 2-dimensional points. Utilizing the Message Passing Interface (MPI) for parallel processing, this implementation is designed to handle large datasets effectively in a distributed computing environment.

## System Requirements
- **Java Development Kit (JDK):** Required for compiling and running Java applications.
- **MPI Implementation:** MPICH is necessary for parallel execution.
- **Bash Shell:** Necessary for executing provided shell scripts.

## Repository Contents
- `KDTree.java`: Implements the KD-Tree in Java.
- `Tuple2D.java`: Represents a 2-dimensional point.
- `compile_mpi.sh`: Bash script for compiling Java files and generating an executable JAR.
- `runmpi_kdtree.sh`: Script for executing the KD-Tree program using MPI on various datasets.

## Installation and Configuration
Ensure that both Java JDK and an MPI implementation are installed on your system. Confirm their installation with the following commands in the terminal:
- Java JDK: `java -version`
- MPI: `mpirun --version`


## Compilation Instructions
Execute the following command to compile the Java source files and generate an executable JAR file:

```bash
./compile_mpi.sh
```

This script compiles KDTree.java and Tuple2D.java, resulting in an executable JAR file named KDTree.jar.

## Execution Guidelines
Utilize the runmpi_kdtree.sh script for executing the KD-Tree MPI program. This script iterates over a set of data files and executes the program with a varying number of MPI processes.

Command format:
```
./runmpi_kdtree.sh
```
This script executes the KD-Tree MPI program for each data file listed in the files array, employing 1, 2, 3, and 4 MPI processes respectively. Execution results are stored in output files named outmpi_[number_of_processes]_[number_of_points].txt.

## Data File Format
Data files should conform to the format specified in the files array of runmpi_kdtree.sh. Each file contains a list of 2D points for processing.

## Output Files
Output files detail the results of range queries performed by the KD-Tree program. These files are named to reflect the number of MPI processes used and the size of the dataset being processed.



