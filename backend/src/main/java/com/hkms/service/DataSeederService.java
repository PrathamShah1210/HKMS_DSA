package com.hkms.service;

import com.hkms.core.nary.NodeType;
import com.hkms.model.entity.CategoryEntity;
import com.hkms.model.entity.PaperEntity;
import com.hkms.repository.CategoryRepository;
import com.hkms.repository.PaperRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Seeds realistic computer science research papers and hierarchical categories
 * if SQLite database is empty.
 */
@Service
public class DataSeederService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeederService.class);

    private final CategoryRepository categoryRepository;
    private final PaperRepository paperRepository;

    public DataSeederService(CategoryRepository categoryRepository, PaperRepository paperRepository) {
        this.categoryRepository = categoryRepository;
        this.paperRepository = paperRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0 && paperRepository.count() > 0) {
            log.info("Database already seeded with {} categories and {} papers.",
                    categoryRepository.count(), paperRepository.count());
            return;
        }

        log.info("Seeding initial academic categories and landmark research papers into SQLite...");

        // 1. Root & Categories
        List<CategoryEntity> categories = new ArrayList<>();
        categories.add(new CategoryEntity("root", "Computer Science Knowledge Base", NodeType.ROOT, null, 0));

        // Subjects
        categories.add(new CategoryEntity("subj-ai", "Artificial Intelligence", NodeType.SUBJECT, "root", 1));
        categories.add(new CategoryEntity("subj-sys", "Systems & Distributed Computing", NodeType.SUBJECT, "root", 2));
        categories.add(new CategoryEntity("subj-dsa", "Data Structures & Algorithms", NodeType.SUBJECT, "root", 3));

        // Topics under AI
        categories.add(new CategoryEntity("top-ml", "Machine Learning", NodeType.TOPIC, "subj-ai", 1));
        categories.add(new CategoryEntity("top-nlp", "Natural Language Processing", NodeType.TOPIC, "subj-ai", 2));

        // Subtopics under ML
        categories.add(new CategoryEntity("sub-dl", "Deep Learning Architectures", NodeType.SUBTOPIC, "top-ml", 1));
        categories.add(new CategoryEntity("sub-rl", "Reinforcement Learning", NodeType.SUBTOPIC, "top-ml", 2));

        // Subtopics under NLP
        categories.add(new CategoryEntity("sub-llm", "Large Language Models & Transformers", NodeType.SUBTOPIC, "top-nlp", 1));

        // Topics under Systems
        categories.add(new CategoryEntity("top-dist", "Distributed Systems", NodeType.TOPIC, "subj-sys", 1));
        categories.add(new CategoryEntity("sub-consensus", "Consensus Protocols", NodeType.SUBTOPIC, "top-dist", 1));
        categories.add(new CategoryEntity("sub-storage", "Distributed Storage", NodeType.SUBTOPIC, "top-dist", 2));

        // Topics under DSA
        categories.add(new CategoryEntity("top-trees", "Tree Data Structures", NodeType.TOPIC, "subj-dsa", 1));
        categories.add(new CategoryEntity("sub-avl", "Balanced Search Trees", NodeType.SUBTOPIC, "top-trees", 1));
        categories.add(new CategoryEntity("top-graphs", "Graph Theory & Algorithms", NodeType.TOPIC, "subj-dsa", 2));
        categories.add(new CategoryEntity("sub-shortest-paths", "Shortest Paths & Traversals", NodeType.SUBTOPIC, "top-graphs", 1));

        categoryRepository.saveAll(categories);

        // 2. Papers
        List<PaperEntity> papers = new ArrayList<>();

        papers.add(createPaper(
                "Attention Is All You Need",
                "Ashish Vaswani, Noam Shazeer, Niki Parmar, Jakob Uszkoreit, Llion Jones, Aidan N. Gomez, Łukasz Kaiser, Illia Polosukhin",
                "The dominant sequence transduction models are based on complex recurrent or convolutional neural networks. We propose the Transformer, a model architecture eschewing recurrence and entirely relying on an attention mechanism.",
                2017, "NeurIPS", "10.48550/arXiv.1706.03762", 98.5, "sub-llm",
                "transformer, attention mechanism, deep learning, nlp, self-attention, neural networks"
        ));

        papers.add(createPaper(
                "Deep Residual Learning for Image Recognition",
                "Kaiming He, Xiangyu Zhang, Shaoqing Ren, Jian Sun",
                "Deeper neural networks are more difficult to train. We present a residual learning framework to ease the training of networks that are substantially deeper than those used previously.",
                2016, "IEEE CVPR", "10.1109/CVPR.2016.90", 96.0, "sub-dl",
                "resnet, residual learning, computer vision, deep learning, image recognition, neural networks"
        ));

        papers.add(createPaper(
                "Language Models are Few-Shot Learners (GPT-3)",
                "Tom B. Brown, Benjamin Mann, Nick Ryder, Melanie Subbiah, Jared Kaplan, Prafulla Dhariwal et al.",
                "Recent work has demonstrated substantial gains on many NLP tasks and benchmarks by pre-training on a large corpus of text. We show that scaling up language models greatly improves few-shot task-agnostic performance.",
                2020, "NeurIPS", "10.48550/arXiv.2005.14165", 97.0, "sub-llm",
                "gpt-3, language models, few-shot learning, transformers, openai, nlp"
        ));

        papers.add(createPaper(
                "BERT: Pre-training of Deep Bidirectional Transformers for Language Understanding",
                "Jacob Devlin, Ming-Wei Chang, Kenton Lee, Kristina Toutanova",
                "We introduce a new language representation model called BERT, which stands for Bidirectional Encoder Representations from Transformers. Unlike recent language representation models, BERT pre-trains deep bidirectional representations.",
                2018, "NAACL-HLT", "10.48550/arXiv.1810.04805", 94.5, "sub-llm",
                "bert, bidirectional transformers, pre-training, fine-tuning, masked lm, nlp"
        ));

        papers.add(createPaper(
                "Mastering the Game of Go with Deep Neural Networks and Tree Search",
                "David Silver, Aja Huang, Chris J. Maddison, Arthur Guez, Laurent Sifre et al.",
                "The game of Go has long been viewed as the most challenging of classic games for artificial intelligence. We introduce AlphaGo, which combines deep neural networks with advanced tree search (MCTS).",
                2016, "Nature", "10.1038/nature16961", 95.0, "sub-rl",
                "alphago, reinforcement learning, monte carlo tree search, deep learning, policy networks, value networks"
        ));

        papers.add(createPaper(
                "In Search of an Understandable Consensus Algorithm (Raft)",
                "Diego Ongaro, John Ousterhout",
                "Raft is a consensus algorithm for managing a replicated log. It produces a result equivalent to (multi-)Paxos, and it is as efficient as Paxos, but its structure is much more understandable and verifiable.",
                2014, "USENIX ATC", "10.5555/2643634.2643666", 93.0, "sub-consensus",
                "raft, consensus algorithm, distributed systems, fault tolerance, leader election, log replication"
        ));

        papers.add(createPaper(
                "The Google File System",
                "Sanjay Ghemawat, Howard Gobioff, Shun-Tak Leung",
                "We have designed and implemented the Google File System (GFS) to meet the rapidly growing demands of Google's data processing needs. GFS shares many of the same goals as previous distributed file systems such as performance, scalability, reliability, and availability.",
                2003, "ACM SOSP", "10.1145/945445.945450", 88.5, "sub-storage",
                "gfs, distributed file systems, google file system, fault tolerance, chunkservers, storage"
        ));

        papers.add(createPaper(
                "An Algorithm for the Organization of Information (AVL Trees)",
                "Georgy Adelson-Velsky, Evgenii Landis",
                "Introduced the first self-balancing binary search tree in computer science literature, ensuring strictly logarithmic worst-case search, insertion, and deletion times through local rotation invariants.",
                1962, "Doklady Akademii Nauk SSSR", "10.1007/978-1-4612-4808-8", 99.0, "sub-avl",
                "avl tree, self-balancing bst, binary search tree, rotations, algorithms, data structures"
        ));

        papers.add(createPaper(
                "A Note on Two Problems in Connexion with Graphs (Dijkstra's Algorithm)",
                "Edsger W. Dijkstra",
                "We consider n points (nodes), some or all pairs of which are connected by a branch. We describe an algorithm to find the path of minimum total length between two given nodes.",
                1959, "Numerische Mathematik", "10.1007/BF01386390", 97.5, "sub-shortest-paths",
                "dijkstra, shortest path algorithm, graph theory, priority queue, spanning tree, algorithms"
        ));

        paperRepository.saveAll(papers);
        log.info("Successfully seeded {} categories and {} landmark papers.", categories.size(), papers.size());
    }

    private PaperEntity createPaper(String title, String authors, String paperAbstract,
                                    int year, String venue, String doi, double priority,
                                    String categoryId, String keywords) {
        PaperEntity p = new PaperEntity();
        p.setTitle(title);
        p.setAuthors(authors);
        p.setPaperAbstract(paperAbstract);
        p.setPublicationYear(year);
        p.setJournalOrConference(venue);
        p.setDoi(doi);
        p.setPriorityScore(priority);
        p.setCategoryId(categoryId);
        p.setKeywords(keywords);
        return p;
    }
}
