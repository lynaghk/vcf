(ns vcfvis.scratch
  (:use [bcbio.variation.api.metrics :only [plot-ready-metrics]]))


(def reference "vendor/bcbio.variation/test/data/GRCh37.fa")
(def vcf "vendor/bcbio.variation/test/data/phasing-reference.vcf")

(plot-ready-metrics vcf reference)

