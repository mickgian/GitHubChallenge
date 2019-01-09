## GitHubChallenge ##
Android App to interact with GitHub API using Dagger2, Retrofit2 and RXJava2

The app has been designed following an MVP pattern 'which adapts with new Android architecture components introduced in Google IO 2017.
It provides lifecycle-aware Presenter with support of LifeCycle and local data source handled by Room' and it is based on the scaffolding 
structure provided here: https://github.com/quangctkm9207/mvp-android-arch-component.

It uses Dagger2, RXJava2, Retrofit, ButterKnife among the most important libraries.

- Once the app starts, it is possible to login via the GitHub login. An Access Token is than recovered and stored into Shared Prefrences. 
  The Access Token increases the limit of request per hours to the GitHubApi from 60/hour to 5000/hour as described
  here: https://developer.github.com/v3/#rate-limiting.
- Despite the best strategy would have been to use the Authenticator API 
  (https://square.github.io/okhttp/3.x/okhttp/okhttp3/Authenticator.html) and modify the http header request, authenticated requests 
  are done by adding query parameters to the endpoints called by services (RepoService, BranchService,etc.)
- The first call is to my GitHub account and shows the repository for this project.
- The approach of recovering repositories and branches and commits is based on the important fact that GitHub api uses a PAGINATION method
  to send data to the applications that request them: https://developer.github.com/v3/#pagination. 
  This strategy implies that if we do a request and specify 1 result per page, we can get the 'Link' attribute in the response http header
  and get from it the number of pages created, hence the number of repositories/branches/commits. For instance: as there's no endpoint 
  to get a total number for commits, we should call all commits paginated for a max of 100 result per page and then count them.
  Or get all the contributors for a repository, then for each contributor get the number of commits and finally sum all of them. 
  Using PAGINATION, instead, we get this number with just 1 call.

- Steps to get all data are the following:

	1. call https://api.github.com/users/:username/repos with a per_page parameter of 1 to get an Observable<Response<List<Headers>>> 
		This give us the total number of repositories per user. It also allow us to deal with 404 and 403 errors before doing any longer call.
		If the number of repositories is more then 100 (GitHub limit of page sent: https://developer.github.com/v3/#pagination),
		we can decide here how many calls we need to get all repositories (NOT IMPLEMENTED IN THE APP, MAX NUMBER OF REPOSITORIES 
    RETURNED IS 100)
		
	2. 	Using RXJava2 3 sequential calls are made: 
			- the FIRST one to	https://api.github.com/users/:owner/repos with a per_page parameter of 100 (this can be tuned to exact
		      number of repositories found with call made in point 1) to get an Observable<List<Repo>>
		    - the SECOND one: for each repository, a call to https://api.github.com/repos/:owner/:repo/branches with a per_page parameter of 1 
		      to get an Observable<Response<List<Headers>>>. From this we get the number of branches.
		      If the 'Link' attribute is not in the Header Response, we have just 1 branch, the master one.
		    - the THIRD call: for each repository, a call to https://api.github.com/repos/:owner/:repo/commits with a per_page parameter of 1 
		      to get an Observable<Response<List<Headers>>>, From this we get the number of commits.
		      
	3. All result are saved into an SQLite DB (Room class is used) so that when there's an ORIENTATION CHANGE of the device no calls 
     are mede to the GitHub API but data are fetched from a local data source. (There's also a cache mechanism not fully implemented)
		
	4. Data are sent to the adapter and showed in a recycler view.
	
- NOTES:
	- For user with lot of repositories loading data is a bit slow. This could be probably improved implementing the Paging library 
    for Android (https://developer.android.com/topic/libraries/architecture/paging/) and using an RXPagedListBuilder 
    (https://developer.android.com/reference/androidx/paging/RxPagedListBuilder).
		
