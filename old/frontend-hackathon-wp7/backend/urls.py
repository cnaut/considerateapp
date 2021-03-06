from django.conf.urls.defaults import patterns, include, url

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    # Examples:
    url(r'^$', 'battles.views.home', name='home'),
    url(r'^allusers', 'battles.views.allusers', name='allusers'),
    url(r'^adduser', 'battles.views.adduser', name='adduser'),
    url(r'^signaluser', 'battles.views.signaluser', name='signaluser'),
    url(r'^receiveuser', 'battles.views.receiveuser', name='receiveuser'),
    url(r'^userform', 'battles.views.userform', name='userform'),
    url(r'^startbattle', 'battles.views.startbattle', name='startbattle'),
    url(r'^declaredefeat', 'battles.views.declaredefeat', name='declaredefeat'),
    url(r'^storetext', 'battles.views.storetext', name='storetext'),
    url(r'^wait', 'battles.views.wait', name='wait'),
    url(r'^mobilecombat/', include('mobilecombat.foo.urls')),

    url(r'^user_photos/(?P<path>.*)$', 'django.views.static.serve', {'document_root': '/home/cnaut/meandmyphone/mobilecombat/user_photos/'}),
	 	
    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    # url(r'^admin/', include(admin.site.urls)),
)
