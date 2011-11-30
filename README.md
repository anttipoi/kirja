# kirja

This is an experiment on Literate Programming with Clojure.

## Usage

Say

    make tangle

to re-create project sources.

Say

    make weave 

to re-create the [documentation](http://anttipoi.github.com/kirja/doc).

## Plans

None. This is just an experiment. 

I learned something from this:

* LP is still a good idea
* double the above for functional languages and cases where code base has strong ideas and dense code
* better tool support is needed before work is comfortable
* workflow is poor: REPL experimentation and TDD both suffer from the extra step of code generation.
* the ns macro gets easily lost when reading in the documentation. It can be hard to see what symbols in code refer to.

## License

Copyright (C) 2011 Teemu Antti-Poika

Distributed under the Eclipse Public License, the same as Clojure.
