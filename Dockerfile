FROM texlive/texlive:latest

RUN tlmgr update --self && \
    tlmgr install jetbrainsmono-otf fontspec

WORKDIR /app

ENTRYPOINT ["xelatex", "-interaction=nonstopmode", "book.tex"]
