from django.db import models
from djangotoolbox.fields import ListField

class User(models.Model):
	name = models.TextField()
	photo = models.FileField(upload_to='user_photos') 
	joined_on = models.DateTimeField(auto_now_add=True, null=True)

class Battle(models.Model):
	users = ListField()
	losers = ListField()
	started_on = models.DateTimeField(auto_now_add=True, null=True)
