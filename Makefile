weave:
	lein run :weave sample-src sample-output

copy-resources:
	cp -R resources/* sample-output

tangle:
	lein run :tangöe sample-src sample-clojure

clean:
	rm -rf sample-output/* sample-clojure/*
