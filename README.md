Memelord App Design Project
===

# Memelord

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Memelord is an app designed to allow people to have a meme-dedicated platform geared at comedy. 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Social Networking App
- **Mobile:** View only. Mobile-first experience.
- **Story:** Memelord came to be to unite various forms of comedy under one realm to be more easily shared and viewed amongst the density of social information present on the internet.
- **Market:** Anyone who enjoys memes and simplified messages in generic formats
- **Habit:** Consistent app logins to view content and share internet-wide
- **Scope:** Facebook/Google login, create photos/texts, and share functionality.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

[x] Authentication/Login
[x] Create posts/texts
[x] View posts/texts
[x] Search for posts
[x] View user posts in a detail view
[x] View more info of post in a detail view
[x] Share post with preview of content to other social mediums
[x] Delete posts
[x] Like posts

**Optional Nice-to-have Stories**

* Reposts user posts on your profile
* Upload videos
* Comunities to join
* UI Animations
* Multiple file types in a post
* Trending/Recommended section in Timeline
[x] Following/Followers feature
[x] Facebook/Twitter/Google SDK
* Hashtags and flter by post hashtags
* Favorites view
* Predefined post formats (Pepega, Pepe, Nani, Omae wa Mou, Kermit, Doge, Success kid, guy looking at girl with girlfriend)
* Edit predefined posts by getting a snapshot of a selection of the UI
* Direct messages
* Like and reply to comments

### 2. Screen Archetypes

* Home Screen
   * View top posts or most recent
* Profile View
   * View user profile when clicked on
   * View user's past posts
* Post View
   * View post details
   * Edit/delete/like/share posts
* Share View
   * Preview of shared content

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home
* My Profile
* Communities
* Trending

**Flow Navigation** (Screen to Screen)

* Home
   * User Profile
   * My Profile
   * Post Details
* Post Details
   * Share View
* Direct Messages
   * DMPerson View
* Trending 
* Communities
   * Community Members View
   * Community Posts View
   * Community Top Posts View

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="IMG_0902.JPG" width=600>

## Schema 
### Models
### 
### Comment
| Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | body          | String   | caption of comment
   | caption       | String   | image caption by author |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |

#### Post

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the user post (default field) |
   | author        | Pointer to User| image author |
   | body          | String   | caption of post
   | image         | File     | image that user posts |
   | caption       | String   | image caption by author |
   | commentsCount | Number   | number of comments that has been posted to an image |
   | likesCount    | Number   | number of likes for the post |
   | createdAt     | DateTime | date when post is created (default field) |
   | updatedAt     | DateTime | date when post is last updated (default field) |
   
### Networking
#### List of network requests by screen
   - Home Feed Screen
      - (Read/GET) Query all recent, recommended posts
      - (Create/POST) Create a new post
      - (Delete) Delete existing post
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete existing comment
   - Post Screen
      - (Create/POST) Create a new comment on a post
      - (Delete) Delete a comment
   - Trending Scren
      - (Create/POST) Creaing a view for a list of most liked posts by category
      - (Delete) Delete a post
   - Create Post Screen
      - (Create/POST) Create a new post object
   - Profile Screen
      - (Read/GET) Query logged in user object
      - (Update/PUT) Update user profile image
      - (Read/GET) Query all recent user posts
      - (Delete) Delete a post
   - Direct Messages Screen
      - (Read/GET) Query messages by date sent
      - (Create/POST) Create a message to send to the user
      
### Daily Milestones to Check-in
##### Maximum of 5 updates
- Progress
- Current plan
- Next phase
- Current struggles
      
#### Weekly Milestones
   | Week                | Milestones    |
   | ------------------- | -------------------------- | 
   | Week 0 (FBU W3)     | Create a basic project plan of the app with an idea                | 
   | Week 1 (FBU W4)     | Add basic views, loginflow, data models, and navigation |
   | Week 2 (FBU W5)     | Implement basic functionalities:<br/>- Share text/images given Android Permissions<br/>- Search for posts<br/>- Likes/Comments/Follow<br/>- Integrate FB/Twitter/Google SDK<br/>- DM users |
   | Week 3 (FBU W6)     | Add complex UI features: <br/>- Get trending posts<br/>- Get recommended posts<br/>- Get hashtags<br/>- Add animations/polish |
   | Week 3 (FBU W7)     | Polish code & design & debug |
   | Week 4 (FBU W8)     | Prepare for presentation |

### Logic Ideas
- Modularize app flow by having a MessageQueue and EventBus with a Pub/Sub
- Use MQ around app to enrich and manage data as data flows downstream

### [BONUS] Digital Wireframes & Mockups

<img src="ezgif-4-90c22aa6d626.gif">

### [BONUS] Interactive Prototype

