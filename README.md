# Arabic Morphological Search Engine

## 📘 Project Description

This project is a mini-project for the **Algorithmics** course (Academic Year 2025–2026).  
Its goal is to design and implement a **morphological search engine for the Arabic language** based on the **root–pattern (root–schème)** system.

The application allows the storage, generation, and validation of Arabic words derived from triliteral roots using efficient data structures and clean object-oriented design in **Java**.

---

## 📋 Features to Implement

### 1. Root Management
- Load Arabic triliteral roots from a text file.
- Insert new roots dynamically.
- Search for a root efficiently using a binary search tree (preferably AVL).
- Display all stored roots in a structured manner.

### 2. Morphological Pattern Management
- Store morphological patterns (schèmes) using a manually implemented hash table.
- Add, modify, and remove patterns.
- Associate each pattern with a transformation rule.

### 3. Morphological Word Generation
- Generate derived Arabic words from:
    - a given root,
    - one or more selected patterns.
- Display generated words clearly (root, pattern, generated word).

### 4. Morphological Validation
- Verify whether a given Arabic word belongs to a specific root.
- Identify the pattern used to generate the word (if valid).
- Return an explicit result (YES / NO).

### 5. Derived Words Management
- Associate each root with a list of validated derived words.
- Update word frequency automatically.
- Display all derived words related to a given root.

---

## 🛠️ Technical Constraints
- Language: **Java**
- Data Structures:
    - AVL Tree (or Binary Search Tree) for roots
    - Hash Table (manual implementation) for patterns
- UI Interface: JavaFX

---

## 🗒️ License

This project is licensed under the terms of the LICENSE file.