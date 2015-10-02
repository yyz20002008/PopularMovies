# PopularMovies
Udacity Nanodegree Android P1

To fetch popular movies, you will use the API from themoviedb.org.

If you don’t already have an account, you will need to create one in order to request an API Key.
https://www.themoviedb.org/?_dc=1437317493

In your request for a key, state that your usage will be for educational/non-commercial use. You will also need to provide some personal information to complete the request. Once you submit your request, you should receive your key via email shortly after.
In order to request popular movies you will want to request data from the /discover/movie endpoint. An API Key is required.
Once you obtain your key, you append it to your HTTP request as a URL parameter like so:

http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=[YOUR API KEY]

You will extract the movie id from this request. You will need this in subsequent requests.

