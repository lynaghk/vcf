(ns vcfvis.data
  (:use-macros [c2.util :only [pp p]])
  (:use [c2.util :only [clj->js]]
        [cljs.reader :only [read-string]]))

;;Stub data for now, generated in scratch.clj
;;NOTE: Desc for DP don't match up. Are they the same metric or not?
(def stub {"vendor/bcbio.variation/test/data/gatk-calls.vcf"
           '{:filename "vendor/bcbio.variation/test/data/gatk-calls.vcf", :created-on #inst "2012-07-03T15:12:41.859-00:00", :metrics ({:vals (0.2727272727272727 0.7272727272727273 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0), :desc "Variant quality score, phred-scaled", :x-scale {:type :linear, :domain [0.0 100000.0]}, :bin-width 5000.0, :y-scale {:type :linear}, :id "QUAL"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [0.0 5000.0]}, :bin-width 250.0, :vals (0.36363636363636365 0.6363636363636364 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0), :id "DP", :desc "Total read depth at the locus"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [0.0 75.0]}, :bin-width 3.75, :vals (0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.18181818181818182 0.2727272727272727 0.18181818181818182 0.36363636363636365 0.0 0.0 0.0 0.0), :id "MQ", :desc "RMS Mapping Quality"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [0.0 200.0]}, :bin-width 10.0, :vals (0.09090909090909091 0.18181818181818182 0.36363636363636365 0.36363636363636365 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0), :id "QD", :desc "Variant Confidence/Quality by Depth"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [0.0 250.0]}, :bin-width 12.5, :vals (0.9090909090909091 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.09090909090909091), :id "HaplotypeScore", :desc "Consistency of the site with at most two segregating haplotypes"})}

           "vendor/bcbio.variation/test/data/freebayes-calls.vcf"
           '{:filename "vendor/bcbio.variation/test/data/freebayes-calls.vcf", :created-on #inst "2012-07-03T15:12:41.963-00:00", :metrics ({:vals (0.2727272727272727 0.18181818181818182 0.2727272727272727 0.18181818181818182 0.09090909090909091 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0), :desc "Variant quality score, phred-scaled", :x-scale {:type :linear, :domain [0.0 100000.0]}, :bin-width 5000.0, :y-scale {:type :linear}, :id "QUAL"} {:y-scale {:type :linear}, :x-scale {:type :linear, :domain [0.0 5000.0]}, :bin-width 250.0, :vals (0.18181818181818182 0.45454545454545453 0.09090909090909091 0.2727272727272727 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0 0.0), :id "DP", :desc "Total read depth at the locus"})}})


(def stub-files
  (let [dirname "vendor/bcbio.variation/test/data"]
    (map (fn [name]
           {:id (str dirname "/" name)
            :folder dirname :filename name
            :created-on #inst "2012-07-03T15:12:41.963-00:00"})
         ["freebayes-calls.vcf" "gatk-calls.vcf"])))


(defn load-metrics [file-urls callback]
  (when (seq file-urls)
    (.get js/jQuery "/api/metrics"
          (clj->js {:file-urls file-urls})
          (fn [d]
            (let [res (read-string d)]
              (callback res))))))


(defn available-files [callback]
  (callback stub-files))
