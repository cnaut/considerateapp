import json
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from django.db import models
from django.db.models import Sum

from phonedata.models import User
from phonedata.models import Stat

from phonedata.forms import UserForm
from phonedata.forms import StatForm
from phonedata.forms import LbSearchForm

def home(request):
    return render_to_response('home.html')

def profile(request):
    return render_to_response('profile.html')

def get_num_checks(checks):
    numchecks = 0
    for check in checks:
	numchecks += int(check.value) 
    return numchecks

def dataview(request):
    users = User.objects.all()
    numusers = users.count()

    stats = Stat.objects.all()
    
    checks = stats.filter(type="checks")
    print(checks.count())
    numchecks = get_num_checks(checks)
    checkssize = checks.count()
    avgchecks = numchecks / checkssize

    return render_to_response('admin.html', {'users': users, 'numusers': numusers, 'avgchecks': avgchecks}, context_instance=RequestContext(request))

def userstats(request, phoneid):
    stats = Stat.objects.filter(user=phoneid).order_by('type')
    
    checks = stats.filter(type="checks")
    numchecks = get_num_checks(checks) 
    checkssize = checks.count()
    avgchecks = numchecks / checkssize

    return render_to_response('userstats.html', {'id': phoneid, 'stats': stats, 'numchecks': numchecks, 'avgchecks': avgchecks}, context_instance=RequestContext(request))

def channel(request):
   return HttpResponse('<script src="//connect.facebook.net/en_US/all.js"></script>')

@csrf_exempt
def adduser(request):
   data = getData(request);
   
   user = User(name=data.get("name"))
   user.save()
   response = user.id

   return HttpResponse(user.id)

@csrf_exempt
def allusers(request):
   users = User.objects.values_list()
   return HttpResponse(users)

@csrf_exempt
def userform(request):
    form = UserForm()
    return render_to_response(
        'userform.html',
        {'form': form, },
        context_instance=RequestContext(request)
    )

@csrf_exempt
def addstat(request):
   data = getData(request);
   stat = Stat(user=data.get("user"), type=data.get("type"), value=data.get("value"))
   stat.save()	 
   return HttpResponse(stat.id)

def addBatchStat(user, time, type, value):
   stat = Stat(user=user, time_recorded=time, type=type, value=value)
   stat.save()

@csrf_exempt
def batchstats(request):
   data = getData(request)
   userid = data['id'] 
   usagedata = data['data']
   for usagestat in usagedata:
      addBatchStat(userid, usagestat['time'], "unlocks", usagestat['unlocks']) 
      addBatchStat(userid, usagestat['time'], "checks", usagestat['checks']) 
      for app in usagestat['apps']:
	addBatchStat(userid, usagestat['time'], "app-" + app['name'] + "-time", app['timeonapp']) 
   return HttpResponse("successful")

@csrf_exempt
def statform(request):
    form = StatForm()
    users = User.objects.all()
    return render_to_response(
        'statform.html',
        {'form': form, 'users': users},
        context_instance=RequestContext(request)
    )

@csrf_exempt
def allstats(request):
   stats = Stat.objects.values_list()
   return HttpResponse(stats)

@csrf_exempt
def leaderboard(request):
   data = getData(request)
   leaders = Stat.objects.values_list().filter(type=data.get('type')).order_by('value') 
   return HttpResponse(leaders)

def lboardsearch(request):
   form = LbSearchForm()
   return render_to_response(
   	'leaderboardsearch.html',
        {'form': form},
        context_instance=RequestContext(request)
   )

def getData(request):
   data = None
   if(request.GET): 
       data = request.GET
   else:
       data = request.POST
       data = data.items()[0][0]
       data = json.loads(data)
   return data	
