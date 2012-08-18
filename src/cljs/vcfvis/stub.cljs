(ns vcfvis.stub
  (:use-macros [c2.util :only [p pp timeout]])
  (:require [cljs.reader :as r]
            [vcfvis.data :as data]))

(def context
  {:username "keminglabs"
   :files '[{:id "gs:/Home/keminglabs/test/freebayes-calls.vcf", :tags ("test"), :folder "/Home/keminglabs/test", :filename "freebayes-calls.vcf", :created-on #inst "2012-07-12T21:06:37.000-00:00"}
            {:id "gs:/Home/keminglabs/test/gatk-calls.vcf", :tags ("test"), :folder "/Home/keminglabs/test", :filename "gatk-calls.vcf", :created-on #inst "2012-07-12T21:06:43.000-00:00"}]

   ;;taken from bcbio.variation.index.metrics
   :metrics {"QUAL" {:range [0.0 10000.0]
                     :desc "Variant quality score, phred-scaled"}
             "DP" {:range [0.0 500.0]
                   :desc "Read depth after filtering of low quality reads"}
             "MQ" {:range [25.0 75.0]
                   :desc "Mapping quality"}
             "QD" {:range [0.0 50.0]
                   :desc "Variant confidence by depth"}
             "HaplotypeScore" {:range [0.0 50.0]
                               :desc "Consistency of the site with at most two segregating haplotypes"}
             "ReadPosEndDist" {:range [0.0 50.0]
                               :desc "Mean distance from either end of read"}
             "AD" {:range [0.0 1.0]
                   :desc "Deviation from expected allele balance for ref/alt alleles"}
             "PL" {:range [-250.0 0]
                   :desc "Normalized, phred-scaled likelihoods for alternative genotype"}}

   })


(defn load-context [callback]
  (timeout 10
           (callback context)))

(defn load-vcf [file-url callback]
  (.getJSON js/$ "big.json"
            (fn [res]
              (callback (data/prep-vcf-json res)))))
