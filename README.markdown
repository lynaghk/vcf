# Variant Visualizer

Web frontend for the [bcbio.variation variant analysis toolkit][1]. Currently 
a work in progress; check out the [demo server][2].

Funding provided by the [Harvard School of Public Health][4] and [EdgeBio][3]; development
by [Keming Labs][5].

## Development

### Build HTML and CSS

Setup bundler (rubygem management gem) and use it to get other Ruby dependencies:

    gem install bundler
    bundle install

To build from haml and sass:

    bundle exec guard

to start the Guard watcher (use Chrome Livereload plugin for auto browser
refresh). Hit return to build the HTML and CSS the first time.

### Build javascript

[Leiningen 2][6] required to build Clojure components. To compile JavaScript
from ClojureScript source, watching for changes and automatically recompiling:

    lein with-profile cljs cljsbuild auto
    
### Starting the server

During development:

    lein ring server-headless

For a production server:
 
    lein run

## License

The code is freely available under the [MIT license][l1].

[1]: https://github.com/chapmanb/bcbio.variation
[2]: http://variantviz.rc.fas.harvard.edu
[3]: http://www.edgebio.com/
[4]: http://compbio.sph.harvard.edu/chb/
[5]: http://keminglabs.com/
[6]: http://leiningen.org/

[l1]: http://www.opensource.org/licenses/mit-license.html

