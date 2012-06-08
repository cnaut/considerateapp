import json
import django.dispatch

from django.shortcuts import render_to_response
from django.template import RequestContext
from django.http import HttpResponse, HttpResponseRedirect
from django.views.decorators.csrf import csrf_exempt
from django.db import models
from django.core import serializers
from battles.models import Battle
from battles.models import User
from battles.forms import UserForm
from django.dispatch import receiver

def home(request):
    return HttpResponse("Mobile Combat Home")

@csrf_exempt
def breakathon(request):
    return render_to_response(
        'breakathon.html',
        {},
        context_instance=RequestContext(request)
    )

@csrf_exempt
def allusers(request):
    users = User.objects.values_list('fb_id', flat=True)
    users = ','.join(users)

    response = HttpResponse(users)
    response['Cache-Control'] = 'no-cache'
    return response

@csrf_exempt
def allbattles(request):
    battles = Battle.objects.all()
   
    battles_json = '{"battles": ['
    first = True 
    for battle in battles:
	battle_json = ""
    	if not first:
		battle_json = ", "
        else:
		first = False
	battle_json = battle_json + '{"id": "' + battle.id + '", "name": "' + battle.name + '", "users": ['
    	battles_json += battle_json 
		
	users = battle.users
	users = ','.join(users)
	
	battles_json += users + "]}"

    battles_json += "]}"
    #json.dumps(battles_json)	    

    return HttpResponse(battles_json)

@csrf_exempt
def adduser(request):
    data = None
    fbid = None
    if(request.POST.get('fbid')):
        data = request.POST
        fbid = data.get('fbid')
    else:
        data = request.raw_post_data
        data = json.loads(data)
        fbid = data['fbid']

    response = "WTF -- added more than once?"
    user = User.objects.filter(fb_id=fbid)
    if(user.count() == 1):
        user[0].active = True
        user[0].save()
        response = user[0].id
    elif(user.count() == 0):
        user = User(fb_id=fbid)
        user.save()
        response = user.id

    return HttpResponse(fbid)


@csrf_exempt
def userform(request):
    form = UserForm()
    return render_to_response(
        'userform.html',
        {'form': form},
        context_instance=RequestContext(request)
    )


@csrf_exempt
def createbattle(request):
    fb_id = request.POST.get('fbid')
    battle_name = request.POST.get('battlename')
    users = [fb_id]
    battle = Battle(creater=fb_id, name=battle_name, users=users)
    battle.save()

    response = battle.id 
    return HttpResponse(response)

@csrf_exempt
def joinbattle(request):
    fb_id = request.POST.get('fbid')
    battle_id = request.POST.get('battle')
    
    battle = Battle.objects.get(id=battle_id)
    battle.users.append(fb_id) 

    return HttpResponse("success")

@csrf_exempt
def startbattle(request):
    data = json.loads(request.raw_post_data)

    users = []
    for user in data['users']:
        users.append(user.encode("ascii"))
    battle = Battle(users=users, losers=[])
    print battle
    battle.save()
    return HttpResponse(battle.id)


@csrf_exempt
def getbattle(request):
    data = json.loads(request.raw_post_data)
    user = data['id']

    battle = Battle.objects.filter(users__contains=user).filter(checkout_time__isnull=True)

    response = "no battle"
    if(battle.count()==1):
        response = battle[0].id

    return HttpResponse(response)


@csrf_exempt
def declaredefeat(request):
    data = request.GET
    battle = Battle.objects.get(id=data.get('battleid'))
    battle.losers.extend([data.get('userid')])
    battle.save()
    print battle.losers
    return HttpResponse(battle.losers)

@csrf_exempt
def location(request):
    lat = request.POST.get('lat')
    long = request.POST.get('long')
    battle = Battle(users=[], losers=[], lat=lat, long=long)
    battle.save()
    return HttpResponse(battle.id)
