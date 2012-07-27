VCF Visualizer
==============

Web frontend for VCF analysis tools in Brad Chapman's [bcbio.variation](https://github.com/chapmanb/bcbio.variation) toolkit.
Currently a work in progress; check out the [temporary dev/evaluation build](http://keminglabs.com/harvard_vcf/).
First official release scheduled for August 2012.

Funding provided by the Harvard School of Public Health and EdgeBio; development by Keming Labs.


Setup
=====

Setup bundler (rubygem management gem)

    gem install bundler

then use it to get other Ruby dependencies

    bundle install

Run

    bundle exec guard

to start the Guard watcher (use Chrome Livereload plugin for auto browser refresh) and

    cd public/
    bundle exec serve

to start a webserver.

[Leiningen 2](https://github.com/technomancy/leiningen/) required to build Clojure components.
First step is to install the bcbio snapshot to your local machine:

    cd vendor/bcbio.variation/
    lein install

Then run

    lein with-profile cljs cljsbuild auto

to compile JavaScript.



