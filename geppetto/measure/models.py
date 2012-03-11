from django.db import models
from djangotoolbox.fields import ListField

# Create your models here.
class TextMessage(models.Model):
	text = models.TextField()
    	sender = models.TextField()
    	receiver = ListField()
    	timestamp = models.DateTimeField(auto_now_add=True, null=True)

class User(models.Model):
	name = models.TextField()
	photo = models.FileField(upload_to='user_photos') 
	joined_on = models.DateTimeField(auto_now_add=True, null=True)

class Activity(models.Model):
	name = models.TextField()	
	users = ListField()
	lat = models.DecimalField(max_digits=8, decimal_places=4, null=True)
	long = models.DecimalField(max_digits=8, decimal_places=4, null=True)
	start_time = models.DateTimeField(auto_now_add=True, null=True)
