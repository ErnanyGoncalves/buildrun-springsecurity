package tech.buildrun.springsecurity.controllers;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.buildrun.springsecurity.controllers.dtos.CreateTweetDTO;
import tech.buildrun.springsecurity.controllers.dtos.FeedDTO;
import tech.buildrun.springsecurity.controllers.dtos.FeedItemDTO;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.entities.Tweet;
import tech.buildrun.springsecurity.repositories.TweetRepository;
import tech.buildrun.springsecurity.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@RestController
public class TweetController {
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDTO> feed(@RequestParam(value = "page",defaultValue = "0") Integer page, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize){
        var tweets = tweetRepository.findAll(PageRequest.of(page,pageSize, Sort.Direction.DESC,"creationTimestamp"))
                .map(tweet -> new FeedItemDTO(tweet.getTweetId(), tweet.getContent(),tweet.getUser().getUsername()));

        return ResponseEntity.ok(new FeedDTO(tweets.getContent(),page,pageSize,tweets.getTotalPages(),tweets.getTotalElements()));

    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDTO dto, JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> deleteTweet(@PathVariable("id") Long tweetId,JwtAuthenticationToken token){
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = tweetRepository.findById(tweetId).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles().stream().anyMatch(role->role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if(isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))){
            tweetRepository.deleteById(tweetId);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok().build();
    }
}
