import json

from django.views.decorators.csrf import csrf_exempt
from django.http import HttpResponse, HttpResponseRedirect

from phonedata.models import User

def home(request):
    return HttpResponse("Welcome to the Considerate App Homepage")

@csrf_exempt
def adduser(request):
    data = None
    fbid = None
    if(request.POST):
        data = request.POST
    if(request.GET):
	data.request.GET

    user = User()
    user.save()
    response = user.id

    return HttpResponse(user.id)
