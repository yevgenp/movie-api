# Getting Started

Application uses Basic authentication with predefined users (username/password):

user1/user1

user2/user2

## Available endpoints

GET ../api/episodes/?title={title}&season={season} - lists all the episodes for a specific season of
series with title

POST ../api/episodes/bookmarked/?id={imdbID} - bookmarks the episode with imdbID

GET ../api/episodes/bookmarked - lists all bookmarked episodes for a specific season of series with
title


