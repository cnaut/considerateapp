from django.conf.urls.defaults import patterns, include, url
from django.contrib.staticfiles.urls import staticfiles_urlpatterns

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    url(r'^$', 'phonedata.views.home', name='home'),
    url(r'^profile$', 'phonedata.views.profile', name='profile'),
    url(r'^admin$', 'phonedata.views.admin', name='admin'),
    url(r'^channel$', 'phonedata.views.channel', name='channel'),
    url(r'^adduser', 'phonedata.views.adduser', name='adduser'),
    url(r'^allusers', 'phonedata.views.allusers', name='alluser'),
    url(r'^userform', 'phonedata.views.userform', name='userform'),
    url(r'^addstat', 'phonedata.views.addstat', name='addstat'),
    url(r'^allstats', 'phonedata.views.allstats', name='allstats'),
    url(r'^batchstats', 'phonedata.views.batchstats', name='batchstats'),
    url(r'^statform', 'phonedata.views.statform', name='statform'),
    url(r'^leaderboard', 'phonedata.views.leaderboard', name='leaderboard'),
    url(r'^lboardsearch', 'phonedata.views.lboardsearch', name='lsearch'),
    # url(r'^considerateapp/', include('considerateapp.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
)
urlpatterns += staticfiles_urlpatterns()
