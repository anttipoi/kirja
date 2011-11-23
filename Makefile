SRC_DIR=sample-src
OUTPUT_DOC_DIR=doc
OUTPUT_SRC_DIR=src

weave:
	lein run :weave $(SRC_DIR) $(OUTPUT_DOC_DIR)

tangle:
	lein run :tangle $(SRC_DIR) $(OUTPUT_SRC_DIR)

clean:
	rm -rf $(OUTPUT_DOC_DIR)/* $(OUTPUT_SRC_DIR)/*

.PHONY: weave tangle clean
