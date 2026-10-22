package com.Crush.journalApp.Service;

import com.Crush.journalApp.Repository.JournalEntryRepository;
import com.Crush.journalApp.entity.JournalEntry;
import com.Crush.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@Component
public class JournalEntryService {

    @Autowired
    private JournalEntryRepository JournalEntryRepository;


    @Autowired
    private UserService userService;

   @Transactional
   public void SaveEntry(JournalEntry journalEntry, String username){
       try {
           User user = userService.findByUserName(username);
           JournalEntry saved = JournalEntryRepository.save(journalEntry);
           user.getJournalEntries().add(saved);
           userService.saveUser(user);
       }catch (Exception e) {
           System.out.print(e);
           throw new RuntimeException("An error occured while saving entry." ,e);
       }
    }


    public void SaveEntry(JournalEntry journalEntry){
        JournalEntryRepository.save(journalEntry);
    }

    public List<JournalEntry> getAll(){
        return JournalEntryRepository.findAll();

    }

    public Optional <JournalEntry> findById(ObjectId id){
        return JournalEntryRepository.findById(id);
    }


    @Transactional
    public boolean DeleteById(ObjectId id ,String username){
       boolean removed = false;
       try {
        User user = userService.findByUserName(username);
        removed = user.getJournalEntries().removeIf(x -> x.getID().equals(id));
        if(removed){
            userService.saveUser(user);
            JournalEntryRepository.deleteById(id);
        }
    }catch (Exception e) {
       System.out.print(e);
       throw new RuntimeException("An error occurred while deleting entry." ,e);
       }
       return removed;
    }

    public User findByUserName(String username){
        return userService.findByUserName(username);
    }

}
