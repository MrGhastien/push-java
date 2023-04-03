OUTDIR = out/production/push
SRCDIR = src

SRCS = $(shell find ./$(SRCDIR) -name "*.java")
SUBDIRS = $(foreach n,$(SRCS),$(dir $n))
CLASSES = $(patsubst ./$(SRCDIR)/%.java,./$(OUTDIR)/%.class,$(SRCS))

.PHONY: run compile clean

all: $(CLASSES)

run: $(CLASSES)
	./run.sh

$(CLASSES): $(SRCS)
	@javac -d ./$(OUTDIR) -cp ./$(OUTDIR) $^

clean:
	rm -rf ./$(OUTDIR)/*
