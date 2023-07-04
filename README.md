# Autopano Path Relativizer

## Problem

Autopano Giga stores its project files with 
absolute paths to the images.    
That means, everything has to be forever at the same place (I meant: path).

## Solution

Use this program to switch to relative paths.   
A file called ```test.pano``` gets a brother called ```test.relative.pano``` 
who is basically the same except the relative paths to the images.    

## Usage

    $ java -jar <jarfile> -u
    $ java -jar <jarfile> <panofile>
    $ java -jar <jarfile> <dir>
