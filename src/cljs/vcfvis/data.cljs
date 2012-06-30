(ns vcfvis.data
  (:use-macros [c2.util :only [pp p]]))

;;Stub data for now, generated in scratch.clj
;;NOTE: Desc for DP don't match up. Are they the same metric or not?
(def stub {"vendor/bcbio.variation/test/data/gatk-calls.vcf"
           '{:filename "vendor/bcbio.variation/test/data/gatk-calls.vcf", :created-on #inst "2012-06-29T18:04:28.552-00:00", :metrics ({:vals (0.09090909090909091 0.0 0.0 0.0 0.0 0.0 0.0 0.09090909090909091 0.0 0.09090909090909091 0.0 0.0 0.09090909090909091 0.0 0.18181818181818182 0.18181818181818182 0.18181818181818182 0.0 0.0 0.09090909090909091), :desc "Variant quality score, phred-scaled", :x-scale {:type :linear, :domain [307.9870000000001 8830.513]}, :bin-width 448.5540000000001, :y-scale {:type :linear}, :id "QUAL"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [171.025 247.975]}, :bin-width 4.049999999999983, :vals (0.09090909090909091 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.09090909090909091 0.0 0.0 0.0 0.0 0.09090909090909091 0.09090909090909091 0.6363636363636364), :id "DP", :desc "Filtered Depth"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [45.73124895095825 59.45874967575073]}, :bin-width 0.7225000381469684, :vals (0.09090909090909091 0.0 0.0 0.09090909090909091 0.0 0.0 0.09090909090909091 0.18181818181818182 0.0 0.0 0.0 0.0 0.0 0.09090909090909091 0.0 0.09090909090909091 0.0 0.0 0.18181818181818182 0.18181818181818182), :id "MQ", :desc "RMS Mapping Quality"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [1.373500020056963 35.32650118991732]}, :bin-width 1.7870000615715977, :vals (0.09090909090909091 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.09090909090909091 0.09090909090909091 0.0 0.0 0.09090909090909091 0.0 0.18181818181818182 0.09090909090909091 0.18181818181818182 0.09090909090909091 0.0 0.09090909090909091), :id "QD", :desc "Variant Confidence/Quality by Depth"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [16.971969604492188 661.9068145751953]}, :bin-width 33.943939208984375, :vals (0.9090909090909091 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.09090909090909091), :id "HaplotypeScore", :desc "Consistency of the site with at most two segregating haplotypes"})}

           "vendor/bcbio.variation/test/data/freebayes-calls.vcf"
           '{:filename "vendor/bcbio.variation/test/data/freebayes-calls.vcf", :created-on #inst "2012-06-29T18:04:28.621-00:00", :metrics ({:vals (0.18181818181818182 0.09090909090909091 0.0 0.0 0.0 0.0 0.09090909090909091 0.0 0.09090909090909091 0.09090909090909091 0.09090909090909091 0.0 0.09090909090909091 0.0 0.09090909090909091 0.09090909090909091 0.0 0.0 0.0 0.09090909090909091), :desc "Variant quality score, phred-scaled", :x-scale {:type :linear, :domain [716.1232500000001 21880.346749999997]}, :bin-width 1113.9065, :y-scale {:type :linear}, :id "QUAL"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [116.075 916.925]}, :bin-width 42.15000000000002, :vals (0.18181818181818182 0.0 0.0 0.0 0.0 0.18181818181818182 0.0 0.09090909090909091 0.18181818181818182 0.0 0.0 0.0 0.09090909090909091 0.0 0.0 0.0 0.09090909090909091 0.09090909090909091 0.0 0.09090909090909091), :id "DP", :desc "Total read depth at the locus"})}})


(defn load [filename]
  (stub filename))

(defn available-filenames []
  (keys stub))
