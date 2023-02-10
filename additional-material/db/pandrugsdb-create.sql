CREATE TABLE `user` (
  `login` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(32) NOT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`login`),
  UNIQUE KEY `UK_ob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `registration` (
  `uuid` varchar(36) NOT NULL,
  `email` varchar(100) NOT NULL,
  `login` varchar(50) NOT NULL,
  `password` varchar(32) NOT NULL,
  PRIMARY KEY (`uuid`),
  UNIQUE KEY `UK_pqp6404l2ndskpsr1xx8eaa68` (`email`),
  UNIQUE KEY `UK_3oq4vywghyclshatote06eku2` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `variants_score_user_computation` (
  `id` varchar(255) NOT NULL,
  `parameter_base_path` varchar(255) DEFAULT NULL,
  `parameter_consequence_filter_active` bit(1) DEFAULT NULL,
  `parameter_gene_frequency_threshold` double DEFAULT NULL,
  `parameter_vcf_file` varchar(255) DEFAULT NULL,
  `results_affectedgenes_file` varchar(255) DEFAULT NULL,
  `results_vep_file` varchar(255) DEFAULT NULL,
  `results_vscore_file` varchar(255) DEFAULT NULL,
  `status_overall_progress` double DEFAULT NULL,
  `status_task_name` varchar(255) DEFAULT NULL,
  `status_task_progress` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `user_login` varchar(50) DEFAULT NULL,
  `results_url_template` varchar(255) DEFAULT NULL,
  `parameter_number_of_input_variants` int(11) DEFAULT NULL,
  `creation_date` datetime DEFAULT NULL,
  `finishing_date` datetime DEFAULT NULL,
  `parameter_pharmcat` bit(1) DEFAULT NULL,
  `parameter_pharmcat_phenotyper_file` varchar(255) DEFAULT NULL,
  `results_pharmcat_report_base` varchar(255) DEFAULT NULL,
  `combined_analysis_cnv_file` varchar(255) DEFAULT NULL,
  `combined_analysis_expression_data_file` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9q5e7lnbfwcoj7mgse2due3ji` (`user_login`),
  CONSTRAINT `FK9q5e7lnbfwcoj7mgse2due3ji` FOREIGN KEY (`user_login`) REFERENCES `user` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pathway` (
  `kegg_id` char(8) NOT NULL,
  `name` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`kegg_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `somatic_mutation_in_cancer` (
  `gene_symbol` varchar(50) NOT NULL,
  `mutation_id` int(11) NOT NULL,
  `sample_id` int(11) NOT NULL,
  `status` varchar(100) NOT NULL,
  `fathmm_prediction` varchar(15) DEFAULT NULL,
  `mutation_aa` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`gene_symbol`,`mutation_id`,`sample_id`,`status`),
  KEY `geneSymbolMutationAAIndex` (`gene_symbol`,`mutation_aa`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `exac` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `allele_frequency` double DEFAULT NULL,
  `alt` varchar(430) DEFAULT NULL,
  `chromosome` varchar(2) DEFAULT NULL,
  `location` int(11) DEFAULT NULL,
  `nfe_allele_frequency` double DEFAULT NULL,
  `ref` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `principal_splice_isoform` (
  `transcript_id` char(15) NOT NULL,
  `isoform_type` varchar(9) NOT NULL,
  PRIMARY KEY (`transcript_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `clinical_genome_variation` (
  `accession` varchar(12) NOT NULL,
  `chromosome` varchar(2) NOT NULL,
  `disease` varchar(150) NOT NULL,
  `end` int(11) NOT NULL,
  `hgvs` varchar(240) NOT NULL,
  `start` int(11) NOT NULL,
  `clinical_significance` varchar(60) NOT NULL,
  `db_snp` varchar(12) DEFAULT NULL,
  PRIMARY KEY (`accession`,`chromosome`,`disease`,`end`,`hgvs`,`start`),
  KEY `dbSnpIndex` (`db_snp`),
  KEY `chromosomePositionsIndex` (`chromosome`,`start`,`hgvs`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pfam` (
  `accession` char(7) NOT NULL,
  `domain_description` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`accession`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein` (
  `uniprot_id` varchar(255) NOT NULL,
  PRIMARY KEY (`uniprot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_pfam` (
  `pfam_accession` char(7) NOT NULL,
  `end` int(11) NOT NULL,
  `start` int(11) NOT NULL,
  `protein_uniprot_id` varchar(255) NOT NULL,
  PRIMARY KEY (`pfam_accession`,`end`,`start`,`protein_uniprot_id`),
  KEY `FKp11yaq7itt06vg103r5j3cgba` (`protein_uniprot_id`),
  CONSTRAINT `FK9snm5o7lw6cliedlu0bs3wgow` FOREIGN KEY (`pfam_accession`) REFERENCES `pfam` (`accession`),
  CONSTRAINT `FKp11yaq7itt06vg103r5j3cgba` FOREIGN KEY (`protein_uniprot_id`) REFERENCES `protein` (`uniprot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `source_info` (
  `source` varchar(50) NOT NULL,
  `curated` bit(1) DEFAULT NULL,
  `short_name` varchar(10) NOT NULL,
  `url_template` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`source`),
  UNIQUE KEY `UK_q9mb7x8r2s9tvc4gdt29tdhfx` (`short_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene` (
  `gene_symbol` varchar(50) NOT NULL,
  `ccle` bit(1) NOT NULL,
  `cgc` bit(1) NOT NULL,
  `driver_level` varchar(255) DEFAULT NULL,
  `driver_gene` varchar(12) DEFAULT NULL,
  `gene_essentiality_score` double DEFAULT NULL,
  `oncodrive_role` varchar(255) NOT NULL,
  `tumor_portal_mutation_level` varchar(255) DEFAULT NULL,
  `oncoscape_score` double DEFAULT NULL,
  `gscore` double DEFAULT NULL,
  PRIMARY KEY (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cancer_domain` (
  `gene_symbol` varchar(50) NOT NULL,
  `code` char(7) NOT NULL,
  PRIMARY KEY (`gene_symbol`,`code`),
  CONSTRAINT `FKcj7ye0l2qq9sighkeaa10682d` FOREIGN KEY (`gene_symbol`) REFERENCES `gene` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `drug` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `extra` varchar(255) DEFAULT NULL,
  `extra_details` varchar(1000) DEFAULT NULL,
  `show_name` varchar(500) NOT NULL,
  `standard_name` varchar(500) NOT NULL,
  `status` varchar(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_standard_name` (`standard_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `family` (
  `drug_id` int(11) NOT NULL,
  `name` varchar(500) DEFAULT NULL,
  KEY `FKqtdkysk9e90sjiv6r5qaba1di` (`drug_id`),
  CONSTRAINT `FKqtdkysk9e90sjiv6r5qaba1di` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `drug_source` (
  `source` varchar(50) NOT NULL,
  `source_drug_name` varchar(500) NOT NULL,
  `drug_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`source`,`source_drug_name`),
  KEY `FKsng7ceirufben6qlmkf0t9e45` (`drug_id`),
  CONSTRAINT `FKic28aylgiww6rkcljgsdhgih5` FOREIGN KEY (`source`) REFERENCES `source_info` (`source`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FKsng7ceirufben6qlmkf0t9e45` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pubchem` (
  `standard_drug_name` varchar(500) NOT NULL,
  `pubchem_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_drug_warning` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `affected_gene` varchar(100) NOT NULL,
  `indirect_gene` varchar(100) DEFAULT NULL,
  `interaction_type` varchar(255) NOT NULL,
  `standard_drug_name` varchar(500) NOT NULL,
  `warning` varchar(1000) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_gene_drug_warning_affected_gene` (`affected_gene`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_drug` (
  `drug_id` int(11) NOT NULL,
  `gene_symbol` varchar(50) NOT NULL,
  `target` bit(1) NOT NULL,
  `resistance` varchar(255) DEFAULT NULL,
  `score` double NOT NULL,
  PRIMARY KEY (`drug_id`,`gene_symbol`,`target`),
  KEY `FK120tk69c0f164ltv1cxq1hp9d` (`gene_symbol`),
  CONSTRAINT `FK120tk69c0f164ltv1cxq1hp9d` FOREIGN KEY (`gene_symbol`) REFERENCES `gene` (`gene_symbol`),
  CONSTRAINT `FKjphgs81q6jshqfeyhcix895os` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_drug_to_drug_source` (
  `drug_id` int(11) NOT NULL,
  `gene_symbol` varchar(50) NOT NULL,
  `target` bit(1) NOT NULL,
  `source` varchar(50) NOT NULL,
  `source_drug_name` varchar(500) NOT NULL,
  `alteration` varchar(1200) DEFAULT NULL,
  PRIMARY KEY (`drug_id`,`gene_symbol`,`target`,`source`,`source_drug_name`),
  KEY `FKgyahdonhj68y94l7r7extwyq0` (`source`,`source_drug_name`),
  CONSTRAINT `FK7p84rcbghdw9h312j0ql8gcu7` FOREIGN KEY (`drug_id`, `gene_symbol`, `target`) REFERENCES `gene_drug` (`drug_id`, `gene_symbol`, `target`),
  CONSTRAINT `FKgyahdonhj68y94l7r7extwyq0` FOREIGN KEY (`source`, `source_drug_name`) REFERENCES `drug_source` (`source`, `source_drug_name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `pathology` (
  `drug_id` int(11) NOT NULL,
  `name` varchar(50) NOT NULL,
  KEY `FKiy02ii2di0ie29afxtk1a5c8a` (`drug_id`),
  CONSTRAINT `FKiy02ii2di0ie29afxtk1a5c8a` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `cancer` (
  `drug_id` int(11) NOT NULL,
  `name` varchar(23) NOT NULL,
  KEY `FKej3htfkav1xki02yi7dolon21` (`drug_id`),
  CONSTRAINT `FKej3htfkav1xki02yi7dolon21` FOREIGN KEY (`drug_id`) REFERENCES `drug` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_entrez` (
  `gene_symbol` varchar(50) NOT NULL,
  `entrez_id` int(11) NOT NULL,
  PRIMARY KEY (`gene_symbol`,`entrez_id`),
  CONSTRAINT `FKfyylhilcxo4ocb5i7wfyu4sg9` FOREIGN KEY (`gene_symbol`) REFERENCES `gene` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_pathway` (
  `gene_gene_symbol` varchar(50) NOT NULL,
  `pathway_kegg_id` char(8) NOT NULL,
  PRIMARY KEY (`gene_gene_symbol`,`pathway_kegg_id`),
  KEY `FK993x9leokj0n4mwqcrivu8mdv` (`pathway_kegg_id`),
  CONSTRAINT `FK993x9leokj0n4mwqcrivu8mdv` FOREIGN KEY (`pathway_kegg_id`) REFERENCES `pathway` (`kegg_id`),
  CONSTRAINT `FKcuqhtb9loh8a9brd4mnbhneks` FOREIGN KEY (`gene_gene_symbol`) REFERENCES `gene` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_gene` (
  `gene_gene_symbol` varchar(50) NOT NULL,
  `gene_interacting_gene_symbol` varchar(50) NOT NULL,
  PRIMARY KEY (`gene_gene_symbol`,`gene_interacting_gene_symbol`),
  KEY `FKcey8kmctctiv7l989jsgpu16v` (`gene_interacting_gene_symbol`),
  CONSTRAINT `FK2viq9soa63okojnm9mcfenxxq` FOREIGN KEY (`gene_gene_symbol`) REFERENCES `gene` (`gene_symbol`),
  CONSTRAINT `FKcey8kmctctiv7l989jsgpu16v` FOREIGN KEY (`gene_interacting_gene_symbol`) REFERENCES `gene` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_change` (
  `protein_change` varchar(10) NOT NULL,
  `uniprot_id` varchar(255) NOT NULL,
  PRIMARY KEY (`protein_change`,`uniprot_id`),
  KEY `IDX4ga0n9hxhbthomqwb7gnv8jvs` (`protein_change`),
  KEY `FK6ic7omqvgs35byvi0ujxh0da` (`uniprot_id`),
  CONSTRAINT `FK6ic7omqvgs35byvi0ujxh0da` FOREIGN KEY (`uniprot_id`) REFERENCES `protein` (`uniprot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `interpro_domain` (
  `id` char(9) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_change_publication` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `authors` varchar(3500) DEFAULT NULL,
  `publication` varchar(200) DEFAULT NULL,
  `title` varchar(400) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_change_publication_protein_change` (
  `publication_id` int(11) NOT NULL,
  `protein_change` varchar(10) NOT NULL,
  `uniprot_id` varchar(255) NOT NULL,
  PRIMARY KEY (`publication_id`,`protein_change`,`uniprot_id`),
  KEY `FK6hixdqh54hk91sgx5peqqvn0` (`protein_change`,`uniprot_id`),
  CONSTRAINT `FK6hixdqh54hk91sgx5peqqvn0` FOREIGN KEY (`protein_change`, `uniprot_id`) REFERENCES `protein_change` (`protein_change`, `uniprot_id`),
  CONSTRAINT `FKj0y4c8iagwoblyi5e1r8h0pys` FOREIGN KEY (`publication_id`) REFERENCES `protein_change_publication` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_interpro_domain` (
  `domain_id` char(9) NOT NULL,
  `end` int(11) NOT NULL,
  `start` int(11) NOT NULL,
  `uniprot_id` varchar(255) NOT NULL,
  PRIMARY KEY (`domain_id`,`end`,`start`,`uniprot_id`),
  KEY `FK3q8y5wgt5c88iaybgvfbeda9h` (`uniprot_id`),
  CONSTRAINT `FK3q8y5wgt5c88iaybgvfbeda9h` FOREIGN KEY (`uniprot_id`) REFERENCES `protein` (`uniprot_id`),
  CONSTRAINT `FK67g4gl3odlqrhly9496cvhylq` FOREIGN KEY (`domain_id`) REFERENCES `interpro_domain` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_gene` (
  `proteins_uniprot_id` varchar(255) NOT NULL,
  `genes_gene_symbol` varchar(50) NOT NULL,
  PRIMARY KEY (`proteins_uniprot_id`,`genes_gene_symbol`),
  KEY `FKe91ri5dqa3owd6fs2ekhm5kkk` (`genes_gene_symbol`),
  CONSTRAINT `FK9sfn848jtoj2tr1vxh2lxgyjy` FOREIGN KEY (`proteins_uniprot_id`) REFERENCES `protein` (`uniprot_id`),
  CONSTRAINT `FKe91ri5dqa3owd6fs2ekhm5kkk` FOREIGN KEY (`genes_gene_symbol`) REFERENCES `gene` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `protein_protein` (
  `protein_uniprot_id` varchar(255) NOT NULL,
  `iteractions_uniprot_id` varchar(255) NOT NULL,
  PRIMARY KEY (`protein_uniprot_id`,`iteractions_uniprot_id`),
  KEY `FKc17wbt3dtg0eivth7nvn7q8ee` (`iteractions_uniprot_id`),
  CONSTRAINT `FK8uvypa2eetqii2wjbfgscmjes` FOREIGN KEY (`protein_uniprot_id`) REFERENCES `protein` (`uniprot_id`),
  CONSTRAINT `FKc17wbt3dtg0eivth7nvn7q8ee` FOREIGN KEY (`iteractions_uniprot_id`) REFERENCES `protein` (`uniprot_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `indirect_gene` (
  `direct_gene_symbol` varchar(50) NOT NULL,
  `drug_id` int(11) NOT NULL,
  `indirect_gene_symbol` varchar(50) NOT NULL,
  `target` bit(1) NOT NULL,
  PRIMARY KEY (`direct_gene_symbol`,`drug_id`,`indirect_gene_symbol`,`target`),
  KEY `FK10f779sjhiiyp187stpq2syky` (`indirect_gene_symbol`),
  KEY `FK9ju2v9tvvi2dqa6aisce6cwb9` (`drug_id`,`direct_gene_symbol`,`target`),
  CONSTRAINT `FK10f779sjhiiyp187stpq2syky` FOREIGN KEY (`indirect_gene_symbol`) REFERENCES `gene` (`gene_symbol`),
  CONSTRAINT `FK9ju2v9tvvi2dqa6aisce6cwb9` FOREIGN KEY (`drug_id`, `direct_gene_symbol`, `target`) REFERENCES `gene_drug` (`drug_id`, `gene_symbol`, `target`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `gene_dependency` (
  `target` bit(1) NOT NULL,
  `gene_dependency_symbol` varchar(50) NOT NULL,
  `drug_id` int NOT NULL,
  `direct_gene_symbol` varchar(50) NOT NULL,
  `alteration` varchar(3) NOT NULL,
  PRIMARY KEY (`target`,`gene_dependency_symbol`,`drug_id`,`direct_gene_symbol`,`alteration`),
  KEY `FK4botv8qfevdnhh457k9jx4afp` (`drug_id`,`direct_gene_symbol`,`target`),
  KEY `FKivkd2eo6kks15johe4algsfae` (`gene_dependency_symbol`),
  CONSTRAINT `FK4botv8qfevdnhh457k9jx4afp` FOREIGN KEY (`drug_id`, `direct_gene_symbol`, `target`) REFERENCES `gene_drug` (`drug_id`, `gene_symbol`, `target`),
  CONSTRAINT `FKivkd2eo6kks15johe4algsfae` FOREIGN KEY (`gene_dependency_symbol`) REFERENCES `gene` (`gene_symbol`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

