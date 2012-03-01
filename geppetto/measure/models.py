from django.db import models

# Create your models here.
class TextMessage(models.Model):
    text = models.TextField()
    sender = models.TextField()
    receiver = models.ListField()
    timestamp = models.DateTimeField()
    media = models.ImageField()
