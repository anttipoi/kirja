weave:
	lein run :weave sample-src sample-output
	cp sample-src/catalog.index sample-output

copy-resources:
	cp -R resources/* sample-output

tangle:
	lein run :tangöe sample-src sample-clojure

clean:
	rm -rf sample-output/* sample-clojure/*
