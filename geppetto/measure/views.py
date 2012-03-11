# Create your views here.
from django.shortcuts import render_to_response
from measure.models import TextMessage
from measure.models import User
from measure.models import Activity 

def home(request):
    message = TextMessage(text="hi", sender="Charles", receiver="Pinokia" )
    return render_to_response('measure/home.html', {'message': message})

@csrf_exempt
def allusers(request):
	users = User.objects.all()
	users = serializers.serialize("json", users)
	
	response = HttpResponse(users)
	response['Cache-Control'] = 'no-cache'
	return response

@csrf_exempt
def adduser(request):
	name = request.POST.get('name')
	user = User(name=name, photo=request.FILES['photo'])
	user.save()
	
	return HttpResponse(user.id)
		
@csrf_exempt
def addactivity(request):
	data = request.POST
	
	name = data.get('activity');

 	users = []
	for user in data.get('users'):
		users.append(user.encode("ascii"))
	
	lat = data.get('lat')
	long = data.get('long')
	
	activity = Activity(name=name, users=users, lat=lat, long=long)
	
	activity.save()
	return HttpResponse(activity.id)
