import json
from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from django.db import models

from phonedata.models import User
from phonedata.models import Stat

from phonedata.forms import UserForm
from phonedata.forms import StatForm

def home(request):
    return HttpResponse("Welcome to the Considerate App")

@csrf_exempt
def adduser(request):
    data = None
    if(request.POST):
	data = request.POST
    if(request.GET):
	data = request.GET

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
   data = None
   if(request.POST):
       data = request.POST
   if(request.GET):
       data = request.GET
   stat = Stat(user=data.get("user"), type=data.get("type"), value=data.get("value"))
   stat.save()	 
   return HttpResponse(stat.id)

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
