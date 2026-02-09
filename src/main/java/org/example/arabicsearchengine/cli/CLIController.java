package org.example.arabicsearchengine.cli;

import org.example.arabicsearchengine.models.DerivedWord;
import org.example.arabicsearchengine.models.Pattern;
import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.models.ValidationResult;
import org.example.arabicsearchengine.repositories.PatternRepository;
import org.example.arabicsearchengine.repositories.RootRepository;
import org.example.arabicsearchengine.services.MorphologyService;
import org.example.arabicsearchengine.services.PatternService;
import org.example.arabicsearchengine.services.RootService;
import org.example.arabicsearchengine.services.ValidationService;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**Command-Line Interface Controller for the Arabic Morphological Search Engine.*/
public class CLIController {
    private final Scanner scanner;
    private final RootService rootService;
    private final PatternService patternService;
    private final MorphologyService morphologyService;
    private final ValidationService validationService;
    private final OutputFormatter formatter;
    private boolean running;

    public CLIController() {
        this.scanner = new Scanner(System.in, "UTF-8");

        // Initialize
        RootRepository rootRepo = new RootRepository();
        PatternRepository patternRepo = new PatternRepository();

        this.rootService = new RootService(rootRepo);
        this.patternService = new PatternService(patternRepo);
        this.morphologyService = new MorphologyService();
        this.validationService = new ValidationService(rootRepo, patternRepo, morphologyService);
        this.formatter = new OutputFormatter();
        this.running = true;

        patternService.initializeDefaultPatterns();
    }

    /**Main entry point*/
    public void run() {
        formatter.printWelcome();

        while (running) {
            printMainMenu();
            int choice = readIntChoice();
            handleMainMenuChoice(choice);
        }

        formatter.printGoodbye();
        scanner.close();
    }

    private void printMainMenu() {
        System.out.println();
        formatter.printHeader("القائمة الرئيسية - Main Menu");
        System.out.println("1. إدارة الجذور (Root Management)");
        System.out.println("2. إدارة الأوزان (Pattern Management)");
        System.out.println("3. توليد الكلمات (Word Generation)");
        System.out.println("4. التحقق الصرفي (Morphological Validation)");
        System.out.println("5. الإحصائيات (Statistics)");
        System.out.println("0. خروج (Exit)");
        formatter.printSeparator();
        System.out.print("اختر رقم الخيار (Enter choice): ");
    }

    private void handleMainMenuChoice(int choice) {
        switch (choice) {
            case 1 -> handleRootManagement();
            case 2 -> handlePatternManagement();
            case 3 -> handleWordGeneration();
            case 4 -> handleValidation();
            case 5 -> handleStatistics();
            case 0 -> running = false;
            default -> formatter.printError("خيار غير صالح (Invalid choice)");
        }
    }

    // ROOT MANAGEMENT

    private void handleRootManagement() {
        while (true) {
            System.out.println();
            formatter.printHeader("إدارة الجذور - Root Management");
            System.out.println("1. تحميل الجذور من ملف (Load roots from file)");
            System.out.println("2. إضافة جذر جديد (Add new root)");
            System.out.println("3. البحث عن جذر (Search for root)");
            System.out.println("4. عرض جميع الجذور (Display all roots)");
            System.out.println("5. حذف جذر (Delete root)");
            System.out.println("0. رجوع (Back)");
            formatter.printSeparator();
            System.out.print("اختر (Choose): ");

            int choice = readIntChoice();
            if (choice == 0) break;

            switch (choice) {
                case 1 -> loadRootsFromFile();
                case 2 -> addNewRoot();
                case 3 -> searchRoot();
                case 4 -> displayAllRoots();
                case 5 -> deleteRoot();
                default -> formatter.printError("خيار غير صالح");
            }
        }
    }

    private void loadRootsFromFile() {
        System.out.print("أدخل مسار الملف (Enter file path): ");
        String path = scanner.nextLine().trim();
        try {
            int count = rootService.loadRootsFromFile(path);
            formatter.printSuccess("تم تحميل " + count + " جذر بنجاح");
        } catch (IOException e) {
            formatter.printError("خطأ في قراءة الملف: " + e.getMessage());
        }
    }

    private void addNewRoot() {
        System.out.print("أدخل الجذر (3 أحرف عربية) - Enter root (3 Arabic letters): ");
        String letters = scanner.nextLine().trim();
        try {
            rootService.addRoot(letters);
            formatter.printSuccess("تمت إضافة الجذر: " + letters);
        } catch (IllegalArgumentException e) {
            formatter.printError(e.getMessage());
        }
    }

    private void searchRoot() {
        System.out.print("أدخل الجذر للبحث (Enter root to search): ");
        String letters = scanner.nextLine().trim();
        Root root = rootService.searchRoot(letters);
        if (root != null) {
            formatter.printSuccess("تم العثور على الجذر!");
            formatter.printRoot(root);
        } else {
            formatter.printError("الجذر غير موجود: " + letters);
        }
    }

    private void displayAllRoots() {
        List<Root> roots = rootService.getAllRoots();
        if (roots.isEmpty()) {
            formatter.printWarning("لا توجد جذور محفوظة");
        } else {
            formatter.printHeader("جميع الجذور (" + roots.size() + ")");
            for (Root root : roots) {
                System.out.println("  • " + root.getRootLetters());
            }
        }
    }

    private void deleteRoot() {
        System.out.print("أدخل الجذر للحذف (Enter root to delete): ");
        String letters = scanner.nextLine().trim();
        if (rootService.rootExists(letters)) {
            rootService.deleteRoot(letters);
            formatter.printSuccess("تم حذف الجذر: " + letters);
        } else {
            formatter.printError("الجذر غير موجود: " + letters);
        }
    }

    // PATTERN MANAGEMENT

    private void handlePatternManagement() {
        while (true) {
            System.out.println();
            formatter.printHeader("إدارة الأوزان - Pattern Management");
            System.out.println("1. عرض جميع الأوزان (View all patterns)");
            System.out.println("2. إضافة وزن جديد (Add new pattern)");
            System.out.println("3. عرض تفاصيل وزن (View pattern details)");
            System.out.println("4. حذف وزن (Delete pattern)");
            System.out.println("5. تعديل وزن (Modify pattern)");
            System.out.println("0. رجوع (Back)");
            formatter.printSeparator();
            System.out.print("اختر (Choose): ");

            int choice = readIntChoice();
            if (choice == 0) break;

            switch (choice) {
                case 1 -> displayAllPatterns();
                case 2 -> addNewPattern();
                case 3 -> viewPatternDetails();
                case 4 -> deletePattern();
                case 5 -> modifyPattern();
                default -> formatter.printError("خيار غير صالح");
            }
        }
    }
    private void modifyPattern() {
        System.out.print("أدخل معرّف الوزن (Enter pattern ID): ");
        String id = scanner.nextLine().trim();

        Pattern existing = patternService.getPattern(id);
        if (existing == null) {
            formatter.printError("الوزن غير موجود: " + id);
            return;
        }

        System.out.println("اترك الحقل فارغًا للاحتفاظ بالقيمة الحالية");

        System.out.print("البنية الجديدة (Current: " + existing.getStructure() + "): ");
        String structure = scanner.nextLine().trim();
        if (structure.isEmpty()) structure = null;

        System.out.print("الوصف الجديد (Current: " + existing.getDescription() + "): ");
        String description = scanner.nextLine().trim();
        if (description.isEmpty()) description = null;

        System.out.print("الفئة الجديدة (Current: " + existing.getCategory() + "): ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) category = null;

        patternService.modifyPattern(id, structure, description, category);
        formatter.printSuccess("تم تعديل الوزن بنجاح: " + id);
    }



    private void displayAllPatterns() {
        List<Pattern> patterns = patternService.getAllPatterns();
        if (patterns.isEmpty()) {
            formatter.printWarning("لا توجد أوزان");
        } else {
            formatter.printHeader("الأوزان المتاحة (" + patterns.size() + ")");
            for (Pattern p : patterns) {
                System.out.println("  • " + p.getPatternId() + " → " + p.getDescription());
            }
        }
    }

    private void addNewPattern() {
        System.out.print("معرّف الوزن (Pattern ID, e.g., فاعل): ");
        String id = scanner.nextLine().trim();
        System.out.print("البنية (Structure using ف-ع-ل): ");
        String structure = scanner.nextLine().trim();
        System.out.print("الوصف (Description): ");
        String desc = scanner.nextLine().trim();
        System.out.print("الفئة (Category): ");
        String cat = scanner.nextLine().trim();

        patternService.addPattern(id, structure, desc, cat);
        formatter.printSuccess("تمت إضافة الوزن: " + id);
    }

    private void viewPatternDetails() {
        System.out.print("أدخل معرّف الوزن (Enter pattern ID): ");
        String id = scanner.nextLine().trim();
        Pattern pattern = patternService.getPattern(id);
        if (pattern != null) {
            formatter.printPattern(pattern);
        } else {
            formatter.printError("الوزن غير موجود: " + id);
        }
    }

    private void deletePattern() {
        System.out.print("أدخل معرّف الوزن للحذف (Enter pattern ID to delete): ");
        String id = scanner.nextLine().trim();
        if (patternService.patternExists(id)) {
            patternService.deletePattern(id);
            formatter.printSuccess("تم حذف الوزن: " + id);
        } else {
            formatter.printError("الوزن غير موجود: " + id);
        }
    }

    // WORD GENERATION

    private void handleWordGeneration() {
        while (true) {
            System.out.println();
            formatter.printHeader("توليد الكلمات - Word Generation");
            System.out.println("1. توليد كلمة من جذر + وزن (Generate from root + pattern)");
            System.out.println("2. توليد جميع المشتقات من جذر (Generate all derivatives)");
            System.out.println("0. رجوع (Back)");
            formatter.printSeparator();
            System.out.print("اختر (Choose): ");

            int choice = readIntChoice();
            if (choice == 0) break;

            switch (choice) {
                case 1 -> generateSingleWord();
                case 2 -> generateAllDerivatives();
                default -> formatter.printError("خيار غير صالح");
            }
        }
    }

    private void generateSingleWord() {
        System.out.print("أدخل الجذر (Enter root): ");
        String rootLetters = scanner.nextLine().trim();

        Root root = rootService.searchRoot(rootLetters);
        if (root == null) {
            // Create temporary root for generation
            try {
                root = new Root(rootLetters);
            } catch (IllegalArgumentException e) {
                formatter.printError("جذر غير صالح: " + e.getMessage());
                return;
            }
        }

        System.out.print("أدخل معرّف الوزن (Enter pattern ID): ");
        String patternId = scanner.nextLine().trim();

        Pattern pattern = patternService.getPattern(patternId);
        if (pattern == null) {
            formatter.printError("الوزن غير موجود: " + patternId);
            return;
        }

        DerivedWord word = morphologyService.generateWord(root, pattern);
        formatter.printHeader("النتيجة - Result");
        System.out.println("  الجذر (Root): " + rootLetters);
        System.out.println("  الوزن (Pattern): " + patternId);
        System.out.println("  الكلمة المشتقة (Derived Word): " + word.getWord());
    }

    private void generateAllDerivatives() {
        System.out.print("أدخل الجذر (Enter root): ");
        String rootLetters = scanner.nextLine().trim();

        Root root;
        try {
            root = new Root(rootLetters);
        } catch (IllegalArgumentException e) {
            formatter.printError("جذر غير صالح: " + e.getMessage());
            return;
        }

        List<Pattern> patterns = patternService.getAllPatterns();
        if (patterns.isEmpty()) {
            formatter.printWarning("لا توجد أوزان متاحة");
            return;
        }

        List<DerivedWord> words = morphologyService.generateAllWords(root, patterns);
        formatter.printHeader("العائلة الصرفية للجذر: " + rootLetters);
        for (DerivedWord word : words) {
            System.out.println("  " + word.getPattern().getPatternId() + " → " + word.getWord());
        }
    }

    // VALIDATION

    private void handleValidation() {
        while (true) {
            System.out.println();
            formatter.printHeader("التحقق الصرفي - Morphological Validation");
            System.out.println("1. التحقق من كلمة مقابل جذر (Validate word against root)");
            System.out.println("2. تحديد جذر ووزن كلمة (Identify root and pattern of word)");
            System.out.println("0. رجوع (Back)");
            formatter.printSeparator();
            System.out.print("اختر (Choose): ");

            int choice = readIntChoice();
            if (choice == 0) break;

            switch (choice) {
                case 1 -> validateWordAgainstRoot();
                case 2 -> identifyWord();
                default -> formatter.printError("خيار غير صالح");
            }
        }
    }

    private void validateWordAgainstRoot() {
        System.out.print("أدخل الكلمة (Enter word): ");
        String word = scanner.nextLine().trim();
        System.out.print("أدخل الجذر (Enter root): ");
        String rootLetters = scanner.nextLine().trim();

        ValidationResult result = validationService.validateWord(word, rootLetters);
        System.out.println();
        System.out.println(result);
    }



    private void identifyWord() {
        System.out.print("أدخل الكلمة لتحليلها (Enter word to analyze): ");
        String word = scanner.nextLine().trim();

        ValidationResult result = validationService.identifyWord(word);
        System.out.println();
        System.out.println(result);
    }

    //STATISTICS

    private void handleStatistics() {
        formatter.printHeader("الإحصائيات - Statistics");
        System.out.println("  عدد الجذور (Root count): " + rootService.getRootCount());
        System.out.println("  عدد الأوزان (Pattern count): " + patternService.getPatternCount());
        System.out.println("  ارتفاع شجرة AVL (AVL Tree height): " +
                rootService.getRepository().getTreeHeight());

        System.out.println();
        System.out.println("إحصائيات جدول التجزئة:");
        patternService.getRepository().printStats();
    }

    // HELPERS

    private int readIntChoice() {
        try {
            String line = scanner.nextLine().trim();
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
