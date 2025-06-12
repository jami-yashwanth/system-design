import java.util.*;

class User {
    String id;
    String name;
    String email;
    String hashedPassword;
    String profilePictureUrl;
    List<User> friends;
    List<FriendRequest> friendRequests;
    Map<String, Post> posts;
    List<Notification> notifications;

    public User(String id, String name, String email, String hashedPassword) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.profilePictureUrl = "";
        this.friends = new ArrayList<>();
        this.friendRequests = new ArrayList<>();
        this.posts = new HashMap<>();
        this.notifications = new ArrayList<>();
    }

    public void setProfilePicture(String url) {
        this.profilePictureUrl = url;
    }

    public void addFriend(User friend) {
        if (friends == null) {
            friends = new ArrayList<>();
        }
        friends.add(friend);
    }

    public void recieveFriendRequest(FriendRequest request) {
        friendRequests.add(request);
    }

    public List<User> getFriends() {
        return friends.stream().toList();
    }

    public List<Post> getPosts() {
        return new ArrayList<>(posts.values());
    }

    public void addNotification(Notification notification) {
        notifications.add(notification);
    }
}

class Comment {
    String id;
    String userId;
    String postId;
    String content;

    public Comment(String id, String userId, String postId, String content) {
        this.id = id;
        this.userId = userId;
        this.postId = postId;
        this.content = content;
    }
}

class Post {
    String id;
    String userId;
    String content;
    List<String> likes;
    List<Comment> comments;

    public Post(String id, String userId, String content) {
        this.id = id;
        this.userId = userId;
        this.content = content;
        this.likes = new ArrayList<>();
        this.comments = new ArrayList<>();
    }

    public void addLike(String userId) {
        if (!likes.contains(userId)) {
            likes.add(userId);
        }
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public void removeLike(String userId) {
        likes.remove(userId);
    }

    public void removeComment(String commentId) {
        comments.removeIf(comment -> comment.id.equals(commentId));
    }

    public List<Comment> getComments() {
        return comments;
    }

    public List<String> getLikes() {
        return likes;
    }

    public String getContent() {
        return content;
    }
}

class FriendRequest {
    String id;
    String fromUserId;
    String toUserId;
    String status; // "pending", "accepted", "rejected"

    public FriendRequest(String id, String fromUserId, String toUserId) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.status = "pending";
    }

    public void accept() {
        this.status = "accepted";
    }

    public void reject() {
        this.status = "rejected";
    }
}


enum NotificationType {
    FRIEND_REQUEST,
    FRIEND_REQUEST_ACCEPTED,
    LIKE,
    COMMENT,
    MENTION
}

class Notification {
    String id;
    String userId;
    NotificationType type;
    String message;
    boolean read;

    public Notification(String id, String userId, NotificationType type, String message) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.message = message;
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }
}

class SocialNetworkService {
    Map<String, User> users = new HashMap<>();
    Map<String, User> usersByEmail = new HashMap<>();
    Map<String, Post> postsMap = new HashMap<>();

    public User register(String name, String email, String password) {
        if(usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("User with this ID already exists.");
        }
        String hashed = hash(password);
        String id = UUID.randomUUID().toString();
        User user = new User(id, name, email, hashed);
        System.out.println("User registered with ID: " + id);
        users.put(id, user);
        usersByEmail.put(email, user);
        return user;
    }

    private String hash(String s) {
        return "hashed_" + s;
    }

    public User login(String email, String password) {
        User user = usersByEmail.get(email);
        if (user == null || !user.hashedPassword.equals(hash(password))) {
            System.out.println("Login failed: Invalid email or password");
            return null;
        }
        System.out.println("Login successful for user: " + user.name);
        return user;
    }

    public User getUserById(String userId) {
        return users.get(userId);
    }

    public void sendFriendRequest(String fromUserId, String toUserId) {
        User fromUser = users.get(fromUserId);
        User toUser = users.get(toUserId);
        if (fromUser != null && toUser != null) {
            String requestId = UUID.randomUUID().toString();
            FriendRequest request = new FriendRequest(requestId, fromUserId, toUserId);
            toUser.recieveFriendRequest(request);
            System.out.println("Friend request sent from " + fromUser.name + " to " + toUser.name);
        } else {
            throw new IllegalArgumentException("Invalid user IDs.");
        }
    }

    public void acceptFriendRequest(String userId, String requestId) {
        User user = users.get(userId);
        if (user != null) {
            for (FriendRequest request : user.friendRequests) {
                if (request.id.equals(requestId) && request.status.equals("pending")) {
                    request.accept();
                    addFriend(userId, request.fromUserId);
                    System.out.println("Friend request accepted from " + request.fromUserId);
                    return;
                }
            }
            throw new IllegalArgumentException("Friend request not found or already processed.");
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public void addFriend(String userId, String friendId) {
        User user = users.get(userId);
        User friend = users.get(friendId);
        if (user != null && friend != null) {
            user.addFriend(friend);
            friend.addFriend(user);
        } else {
            throw new IllegalArgumentException("User or friend not found.");
        }
    }

    public List<User> getFriends(String userId) {
        User user = users.get(userId);
        if (user != null && user.friends != null) {
            List<User> friendsList = new ArrayList<>();
            for (User friend : user.friends) {
                if (friend != null) {
                    friendsList.add(friend);
                }
            }
            return friendsList;
        }
        return new ArrayList<>();
    }

    public void setProfilePicture(String userId, String url) {
        User user = users.get(userId);
        if (user != null) {
            user.setProfilePicture(url);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public Post createPost(String userId, String content) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        String postId = UUID.randomUUID().toString();
        Post post = new Post(postId, userId, content);
        user.posts.put(postId, post);
        postsMap.put(postId, post);
        return post;
    }

    public void likePost(String userId, String postId) {
        User user = users.get(userId);
        Post post = postsMap.get(postId);
        if (user == null || post == null) {
            throw new IllegalArgumentException("User or post not found.");
        }
        post.addLike(userId);
    }

    public void commentOnPost(String userId, String postId, String content) {
        User user = users.get(userId);
        Post post = postsMap.get(postId);
        if (user == null || post == null) {
            throw new IllegalArgumentException("User or post not found.");
        }
        String commentId = UUID.randomUUID().toString();
        Comment comment = new Comment(commentId, userId, postId, content);
        post.addComment(comment);
    }

    public void removeLike(String userId, String postId) {
        User user = users.get(userId);
        Post post = postsMap.get(postId);
        if (user == null || post == null) {
            throw new IllegalArgumentException("User or post not found.");
        }
        post.removeLike(userId);
    }

    public void removeComment(String userId, String postId, String commentId) {
        User user = users.get(userId);
        Post post = postsMap.get(postId);
        if (user == null || post == null) {
            throw new IllegalArgumentException("User or post not found.");
        }
        post.removeComment(commentId);
    }

    public List<Post> getPostsByUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user.getPosts();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<Post> getNewsFeed(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        List<Post> newsFeed = new ArrayList<>();
        for (User friend : user.getFriends()) {
            newsFeed.addAll(friend.getPosts());
        }
        return newsFeed;
    }

    public void sendNotification(String userId, NotificationType type, String message) {
        String notificationId = UUID.randomUUID().toString();
        Notification notification = new Notification(notificationId, userId, type, message);
        User user = users.get(userId);
        if (user != null) {
            user.addNotification(notification);
            System.out.println("Notification sent to " + user.name + ": " + message);
        } else {
            throw new IllegalArgumentException("User not found.");
        }
    }

    public List<Notification> getNotifications(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        return user.notifications.stream().filter(n -> !n.read).toList();
    }

    public void markNotificationAsRead(String userId, String notificationId) {
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        for (Notification notification : user.notifications) {
            if (notification.id.equals(notificationId)) {
                notification.markAsRead();
                System.out.println("Notification marked as read: " + notification.message);
                return;
            }
        }
        throw new IllegalArgumentException("Notification not found.");
    }
}


public class SocialNetworkApp {
    public static void main(String[] args) {
        SocialNetworkService network = new SocialNetworkService();

        // 1. Register users
        User alice = network.register("Alice", "alice@mail.com", "pass123");
        User bob = network.register("Bob", "bob@mail.com", "pass123");
        User charlie = network.register("Charlie", "charlie@mail.com", "pass123");

        // 2. Login
        network.login("alice@mail.com", "pass123");
        network.login("bob@mail.com", "pass23");

        // 3. Friend Request from Alice → Bob
        network.sendFriendRequest(alice.id, bob.id);
        // Bob accepts it
        System.out.println("Bob's Friend Requests:" + 
        bob.friendRequests.stream().map(req -> req.fromUserId).toList());
        FriendRequest bobRequest = bob.friendRequests.get(0);
        network.acceptFriendRequest(bob.id, bobRequest.id);

        // 4. Alice creates a post
        Post alicePost = network.createPost(alice.id, "Hello world from Alice!");

        // 5. Bob likes and comments on Alice's post
        network.likePost(bob.id, alicePost.id);
        network.commentOnPost(bob.id, alicePost.id, "Nice post!");

        // 6. Newsfeed for Bob
        List<Post> bobsFeed = network.getNewsFeed(bob.id);
        System.out.println("\n📰 Bob's Newsfeed:");
        for (Post post : bobsFeed) {
            System.out.println("Post by " + post.userId + ": " + post.content);
            System.out.println("Likes: " + post.getLikes().size());
            for (Comment comment : post.getComments()) {
                System.out.println("Comment: " + comment.content);
            }
        }

        // 7. Notifications for Alice
        System.out.println("\n🔔 Alice's Notifications:");
        List<Notification> aliceNotifs = network.getNotifications(alice.id);
        for (Notification n : aliceNotifs) {
            System.out.println(n.message);
        }

        // 8. Alice marks notification as read
        if (!aliceNotifs.isEmpty()) {
            network.markNotificationAsRead(alice.id, aliceNotifs.get(0).id);
        }

        System.out.println("\n✅ Simulation Complete.");
    }
}

/*

UML Diagram:

+---------------------+
|       User          |
+---------------------+
| - id: String        |
| - name: String      |
| - email: String     |
| - hashedPassword: String |
| - profilePictureUrl: String |
| - friends: List<User> |
| - friendRequests: List<FriendRequest> |
| - posts: Map<String, Post> |
| - notifications: List<Notification> |
+---------------------+
| +setProfilePicture(url: String): void |
| +addFriend(friend: User): void        |
| +recieveFriendRequest(request: FriendRequest): void |
| +getFriends(): List<User>            |
| +getPosts(): List<Post>              |
| +addNotification(notification: Notification): void |
+---------------------+

+---------------------+
|     FriendRequest   |
+---------------------+
| - id: String        |
| - fromUserId: String|
| - toUserId: String  |
| - status: String    |
+---------------------+
| +accept(): void     |
| +reject(): void     |
+---------------------+

+---------------------+
|        Post         |
+---------------------+
| - id: String        |
| - userId: String    |
| - content: String   |
| - likes: List<String> |
| - comments: List<Comment> |
+---------------------+
| +addLike(userId: String): void        |
| +addComment(comment: Comment): void   |
| +removeLike(userId: String): void     |
| +removeComment(commentId: String): void |
| +getComments(): List<Comment>         |
| +getLikes(): List<String>             |
| +getContent(): String                 |
+---------------------+

+---------------------+
|       Comment       |
+---------------------+
| - id: String        |
| - userId: String    |
| - postId: String    |
| - content: String   |
+---------------------+

+-----------------------------+
|      Notification           |
+-----------------------------+
| - id: String                |
| - userId: String            |
| - type: NotificationType    |
| - message: String           |
| - read: boolean             |
+-----------------------------+
| +markAsRead(): void         |
+-----------------------------+

+-----------------------------+
|    <<enum>> NotificationType|
+-----------------------------+
| FRIEND_REQUEST              |
| FRIEND_REQUEST_ACCEPTED     |
| LIKE                        |
| COMMENT                     |
| MENTION                     |
+-----------------------------+

+-----------------------------+
|  SocialNetworkService       |
+-----------------------------+
| - users: Map<String, User>  |
| - usersByEmail: Map<String, User> |
| - postsMap: Map<String, Post>     |
+-----------------------------+
| +register(...)              |
| +login(...)                 |
| +sendFriendRequest(...)     |
| +acceptFriendRequest(...)   |
| +createPost(...)            |
| +likePost(...)              |
| +commentOnPost(...)         |
| +removeLike(...)            |
| +removeComment(...)         |
| +getNewsFeed(...)           |
| +sendNotification(...)      |
| +markNotificationAsRead(...)|
+-----------------------------+

================================

DB Schema:

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    hashed_password VARCHAR(255),
    profile_picture_url VARCHAR(255)
);

CREATE TABLE friend_requests (
    id VARCHAR(36) PRIMARY KEY,
    from_user_id VARCHAR(36),
    to_user_id VARCHAR(36),
    status ENUM('pending', 'accepted', 'rejected'),
    FOREIGN KEY (from_user_id) REFERENCES users(id),
    FOREIGN KEY (to_user_id) REFERENCES users(id)
);

CREATE TABLE posts (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    content TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE post_likes (
    post_id VARCHAR(36),
    user_id VARCHAR(36),
    PRIMARY KEY (post_id, user_id),
    FOREIGN KEY (post_id) REFERENCES posts(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    post_id VARCHAR(36),
    content TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE notifications (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36),
    type ENUM('FRIEND_REQUEST', 'FRIEND_REQUEST_ACCEPTED', 'LIKE', 'COMMENT', 'MENTION'),
    message TEXT,
    read BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

 */