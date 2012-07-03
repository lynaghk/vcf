#!/bin/bash

rsync -avk \
  --exclude vendor/bcbio* \
  public/  ws:keminglabs_v2/harvard_vcf
