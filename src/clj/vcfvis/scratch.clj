(ns vcfvis.scratch
  (:use [bcbio.variation.api.metrics :only [plot-ready-metrics]]))


(def reference "vendor/bcbio.variation/test/data/GRCh37.fa")
(def vcf1 "vendor/bcbio.variation/test/data/gatk-calls.vcf")
(def vcf2 "vendor/bcbio.variation/test/data/freebayes-calls.vcf")

[(plot-ready-metrics vcf1 reference)
 (plot-ready-metrics vcf2 reference)]



