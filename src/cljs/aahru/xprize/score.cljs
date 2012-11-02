;;Interactive functionality for X prize scoring based web pages.

(ns aahru.xprize.score
  (:require [clojure.string :as string]
            [c2.dom :as dom]
            [chosen.core :as chosen]
            [crate.core :as crate]
            [shoreleave.remotes.http-rpc :as rpc]
            [goog.string :as gstring]
            [goog.Timer :as timer])
  (:require-macros [shoreleave.remotes.macros :as sl]))

;; ## Display scoring results

(defn- progress-percent
  "Rough progress points to indicate status of processing."
  [desc]
  (cond
   (gstring/startsWith desc "Starting variation") 10
   (gstring/startsWith desc "Prepare VCF, resorting to genome build: contestant") 15
   (gstring/startsWith desc "Normalize MNP and indel variants: contestant") 60
   (gstring/startsWith desc "Comparing VCFs: reference vs contestant") 75
   (gstring/startsWith desc "Summarize comparisons") 90
   (gstring/startsWith desc "Finished") 100
   :else nil))

(defn ^:export update-run-status
  "Update summary page with details about running statuses."
  [run-id]
  (sl/rpc (get-status run-id) [info]
          (if (= :finished (:state info))
            (sl/rpc (get-summary run-id) [sum-html]
                    (if (nil? sum-html)
                      (timer/callOnce (fn [] (update-run-status run-id)) 2000)
                      (-> (dom/select "#scoring-in-process")
                          (dom/replace! sum-html))))
            (do
              (when-not (nil? info)
                (-> (dom/select "#scoring-status")
                    (dom/replace! (crate/html [:p (:desc info)])))
                (when-let [pct (progress-percent (:desc info))]
                  (-> (dom/select "#scoring-progress")
                      (dom/attr :style (str "width: " pct "%")))))
              (timer/callOnce (fn [] (update-run-status run-id)) 2000)))))

;; ## Retrieve remote file information

(defn- gs-paths-to-chosen [xs]
  (map (fn [x] {:value (:full x) :text (:name x)}) xs))

(defn- update-gs-files!
  "Update file information based on parent"
  [file-chosen file-id dir ftype]
  (let [final-form-id (str "#" (string/join "-" (cons "gs" (rest (string/split file-id #"-")))))]
    (sl/rpc ("variant/external-files" dir ftype) [files]
            (chosen/options file-chosen (gs-paths-to-chosen files))
            (-> (dom/select final-form-id)
                (dom/val (chosen/selected file-chosen)))
            (add-watch file-chosen :change
                       (fn [fname]
                         (-> (dom/select final-form-id)
                             (dom/val fname)))))))

(defn prep-remote-selectors
  "Prepare dropdowns for retrieval via GenomeSpace or Galaxy."
  [select-id ftype]
  (let [folder-id (str select-id "-folder")
        file-id (str select-id "-file")]
    (let [folder-chosen (chosen/ichooseu! (str "#" folder-id))
          file-chosen (chosen/ichooseu! (str "#" file-id))]
      (sl/rpc ("variant/external-dirs") [dirs]
              (chosen/options folder-chosen (gs-paths-to-chosen dirs))
              (when-let [cur-dir (chosen/selected folder-chosen)]
                (update-gs-files! file-chosen file-id cur-dir ftype))
              (add-watch folder-chosen :change
                         (fn [dir]
                           (update-gs-files! file-chosen file-id dir ftype)))))))

(defn- prep-genome-selector
  "Prepare genome selector to pick analysis genome."
  []
  (let [genome-chosen (chosen/ichooseu! "#comparison-genome")]
    (sl/rpc ("meta/genomes") [genomes]
            (chosen/options genome-chosen genomes))))

(defn ^:export setup-remotes 
  "Setup retrieval of file information from GenomeSpace and Galaxy."
  []
  (prep-genome-selector)
  (prep-remote-selectors "variant" "vcf")
  (prep-remote-selectors "region" "bed"))
