package org.example.arabicsearchengine.services;

import org.example.arabicsearchengine.models.Root;
import org.example.arabicsearchengine.repositories.RootRepository;
import org.example.arabicsearchengine.utils.FileLoader;

import java.io.IOException;
import java.util.List;

public class RootService {
    private final RootRepository rootRepository;

    public RootService(RootRepository rootRepository) {
        this.rootRepository = rootRepository;
    }


    public int loadRootsFromFile(String filePath) throws IOException {
        FileLoader.populateRootRepository(rootRepository, filePath);
        return rootRepository.count();
    }


    public void addRoot(String rootLetters) {
        if (rootLetters == null || rootLetters.length() != 3) {
            throw new IllegalArgumentException("Root must be exactly 3 letters");
        }
        Root root = new Root(rootLetters);
        rootRepository.save(root);
    }

    public Root searchRoot(String rootLetters) {
        return rootRepository.findByLetters(rootLetters);
    }

    public boolean rootExists(String rootLetters) {
        return rootRepository.exists(rootLetters);
    }

    public void deleteRoot(String rootLetters) {
        rootRepository.delete(rootLetters);
    }

    public List<Root> getAllRoots() {
        return rootRepository.findAll();
    }

    public int getRootCount() {
        return rootRepository.count();
    }

    /**Gets the underlying repository.*/
    public RootRepository getRepository() {
        return rootRepository;
    }
    public Root findByLetters(String letters) {
        return rootRepository.findByLetters(letters);
    }
}
