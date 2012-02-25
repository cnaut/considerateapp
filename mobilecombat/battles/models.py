from django.db import models

class User(models.Model):
	name = models.TextField()
	photo = models.ImageField(upload_to='user_photos') 
	joined_on = models.DateTimeField(auto_now_add=True, null=True)

class Battle(models.Model):
	text = models.TextField()
