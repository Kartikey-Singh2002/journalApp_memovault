package com.Crush.journalApp.controller;

import com.Crush.journalApp.Service.JournalEntryService;
import com.Crush.journalApp.Service.UserService;
import com.Crush.journalApp.entity.JournalEntry;
import com.Crush.journalApp.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
public class JournalEntryController {


    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?>getAllJournalEntriesOfUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);
            List<JournalEntry> all= user.getJournalEntries();
            if(all != null && !all.isEmpty()) {
                return new ResponseEntity<>(all , HttpStatus.OK);
            }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<JournalEntry> CreateEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            myEntry.setDate(LocalDateTime.now());
            journalEntryService.SaveEntry( myEntry , userName);
            return new ResponseEntity<>(myEntry,HttpStatus.CREATED) ;
        } catch (Exception e) {

            return new ResponseEntity<JournalEntry>(HttpStatus.BAD_REQUEST) ;
        }
    }

    @GetMapping("ID/{myid}")
    public ResponseEntity<?> getJournalEntryByID(@PathVariable ObjectId myid) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();

        User user = userService.findByUserName(userName);

        List<JournalEntry> collect = user.getJournalEntries()
                .stream()
                .filter(x -> x.getID().equals(myid))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {
            Optional<JournalEntry> journalEntry = journalEntryService.findById(myid);

            if (journalEntry.isPresent()) {
                return new ResponseEntity<>(journalEntry.get(), HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("ID/{myid}")
    public ResponseEntity<?> deleteJournalEntryByID(@PathVariable ObjectId myid ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
       boolean removed= journalEntryService.DeleteById( myid , userName );
       if(removed) {
           return new ResponseEntity<>(HttpStatus.NO_CONTENT);
       }else {
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
       }
    }

    @PutMapping("ID/{myid}")
    public ResponseEntity<?> updateJournalEntryByID(
            @PathVariable ObjectId myid,
            @RequestBody JournalEntry newEntry) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userService.findByUserName(userName);

        List<JournalEntry> collect = user.getJournalEntries()
                .stream()
                .filter(x -> x.getID().equals(myid))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) {

            Optional<JournalEntry> journalEntry = journalEntryService.findById(myid);

            if (journalEntry.isPresent()) {

                JournalEntry old = journalEntry.get();

                old.setTitle(
                        newEntry.getTitle() != null && !newEntry.getTitle().equals("")
                                ? newEntry.getTitle()
                                : old.getTitle()
                );

                old.setContent(
                        newEntry.getContent() != null && !newEntry.getContent().equals("")
                                ? newEntry.getContent()
                                : old.getContent()
                );

                journalEntryService.SaveEntry(old);

                return new ResponseEntity<JournalEntry>(old, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
