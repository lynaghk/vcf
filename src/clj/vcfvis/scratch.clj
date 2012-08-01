(ns vcfvis.scratch
  (:use [bcbio.variation.api.metrics :only [plot-ready-metrics]]
        [bcbio.variation.api.file :only [get-files]]
        [bcbio.variation.api.run :only [do-analysis]]))


(def reference "vendor/bcbio.variation/test/data/GRCh37.fa")
(def vcf1 "vendor/bcbio.variation/test/data/gatk-calls.vcf")
(def vcf2 "vendor/bcbio.variation/test/data/freebayes-calls.vcf")

[(plot-ready-metrics vcf1 reference)
 (plot-ready-metrics vcf2 reference)]

(def creds {:username "keminglabs" :password "vcftest"})
(def files (get-files :vcf creds))

(plot-ready-metrics (:id (first files)) reference
                    :creds creds
                    :cache-dir "/tmp/")

(do-analysis :filter
             {:filename (:id (first files))
              :metrics {"DP" [0 100]}}
             creds)
