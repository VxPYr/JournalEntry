package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.repository.JournalEntryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved); // After saving journal entry inside journal_entries, we add it to the arraylist created inside User
            userService.saveUser(user); // we are saving the user now into the users collection
        } catch (Exception e) {
            log.error("Exception: ", e);
            throw e;
        }
    }

    public void saveEntry(JournalEntry journalEntry) {
        journalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAllJournalEntry() {
        return journalEntryRepository.findAll();
    }

    public Optional<JournalEntry> getJournalEntryById(ObjectId id) {
        return journalEntryRepository.findById(id);
    }

    @Transactional
    public boolean deleteJournalEntryById(String username, ObjectId id) {
        User user = userService.findByUsername(username);
        boolean removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        if(removed) {
            userService.saveNewUser(user);
            journalEntryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public JournalEntry updateJournalEntry(JournalEntry journalEntry) {
        return journalEntryRepository.save(journalEntry);
    }



}
