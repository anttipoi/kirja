SRC_DIR=sample-src
OUTPUT_DOC_DIR=doc
OUTPUT_SRC_DIR=src

weave:
	lein run :weave $(SRC_DIR) $(OUTPUT_DOC_DIR)

tangle:
	lein run :tangle $(SRC_DIR) $(OUTPUT_SRC_DIR)

# Do not clean src dir, since otherwise kirja won't run
clean:
	rm -rf $(OUTPUT_DOC_DIR)/* 

.PHONY: weave tangle clean
