# pandrugs-backend
A Java backend for the PanDrugs.

## Project description
**PanDrugs** provides a platform **to guide the selection of therapies from the results of genome-wide studies in cancer 
disease**.

Using 4 alternative inputs (e.g. standard VCF files, RNK files, gene list and drug query), **PanDrugs identify
actionable molecular alterations and prioritize drugs by calculating gene-drug scores** which takes into account:
i) genomic feature evidence by mutation impact score; ii) target pathway context; iii) drug approval status (FDA,
clinical trial or experimental small molecule inhibitors) and iv) manually-curated pharmacological information retrieved
from the literature.

**PanDrugs scores combines biological and clinical relevance of the genes and their susceptibility to be targeted**
reflecting the strength or evidence level of the gene-drug association in order to assist the clinical decision making.

PanDrugs current version integrates data from 18 primary sources and supports ~50,000 drug-target associations obtained
from ~6,000 genes and ~11,000 unique compounds.