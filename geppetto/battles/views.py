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
from gevent.event import Event 


class Signals(object):
	def __init__(self):
		self.new_user_event = Event()

	def signaluser(self, request):
		user_id = request.GET.get('id')	
		print self.new_user_event.is_set()	
		self.new_user_event.set()
		self.new_user_event.clear()
		return HttpResponse("New User Signaled")

	def receiveuser(self, request):
		self.new_user_event.wait()
		return HttpResponse("New User Received")

signal = Signals()
signaluser = signal.signaluser
receiveuser = signal.receiveuser

def home(request):
	return HttpResponse("Moble Combat Home")

@csrf_exempt
def adduser(request):
	data = None
	name = None
	if(request.POST.get('name')):
		data = request.POST
		name = data.get('name')
	else:		
		data = request.raw_post_data
		data = json.loads(data)
		name = data['name']	
		

	user = User(name=name, photo=request.FILES['photo'])
	user.save()
	
	return HttpResponse(user.id)

@csrf_exempt
def allusers(request):
	users = User.objects.all()
	users = serializers.serialize("json", users)
	
	response = HttpResponse(users)
	response['Cache-Control'] = 'no-cache'
	return response

@csrf_exempt
def userform(request):
	form = UserForm() 
	return render_to_response(
		'userform.html',
		{'form': form},
		context_instance=RequestContext(request)
	)


@csrf_exempt
def startbattle(request):
	data = request.raw_post_data
	data = json.loads(data)

	
 	users = []
	for user in data['users']:
		users.append(user.encode("ascii"))
	battle = Battle(users=users, losers=[])
	print battle
	battle.save()
	return HttpResponse(battle.id)

@csrf_exempt
def getbattle(request):
	data = request.raw_post_data
	data = json.loads(data)
	battle = Battle.objects.get()	
	return HttpResponse(battle.id)


@csrf_exempt
def declaredefeat(request):
	data = request.GET
	battle = Battle.objects.get(id=data.get('battleid'))	
	battle.losers.extend([data.get('userid')]) 
	battle.save()
	print battle.losers
	return HttpResponse(battle.losers)

@csrf_exempt
def wait(request):
	while True:	
		print "hi"	
