#!/bin/bash
#Download some larger VCF files and reference genomes (600+ MB!)
wget -nc https://s3.amazonaws.com/chapmanb/NA19239-freebayes.vcf.gz
wget -nc https://s3.amazonaws.com/biodata/genomes/GRCh37-seq.tar.xz
