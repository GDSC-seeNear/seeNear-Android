# SeeNear
Participate 2023 Google Solution Challenge  

We belong to [GDSC Seoultech](https://gdsc.community.dev/seoul-national-university-of-science-and-technology/).
</br>
## What is SeeNear?
Modern society is transitioning to an aging society across the globe, with South Korea in particular estimated to have a senior citizen ratio of about 40% in 2060. The problem of the elderly living alone is particularly acute, with about 60% of the elderly living alone currently aged 65 or older, a number that is expected to increase in the future. However, only 16% of seniors are currently receiving help, and the number of seniors managed by each social worker is 25.  
So SeeNear aims to provide the elderly with periodic alarms and chatbots through the application to continuously check their health and psychological status. The name of SeeNear combines the words "보다" for "see" and "가까이" for "near", which means to help seniors up close.
</br>
## Feature
### Sign up  
SeeNear uses a phone number to sign up for seniors, and we also use phone numbers to link seniors and their caregivers' accounts. This is to accommodate seniors who may not be familiar with OAuth like email.
</br>
### User
Users are asked questions about their health or psychological state through three alarms: morning, noon, and evening. Important but simple questions are answered by checking an O or X through a checkbox. If the user needs to talk about a specific case, they can answer the question with their voice through the Speech-to-Text API. The chatbot will then give the appropriate answer, and the user's input will be delivered to caregiver in the form of a report.
</br>
### Caregiver
The caregiver receives the user's answers in the form of a report with statistics. The report highlights keywords such as hospitals, body parts, and diseases, and visualizes sentiment scores to make it easier for caregiver to understand. It also includes information about medications the user needs to take so they don't forget. 


## Demo Video
[![SeeNear](https://user-images.githubusercontent.com/54880474/229173623-2b5241ae-dc6c-488f-8b63-a9108537ed76.jpg)](https://youtu.be/JpiwWp_myq8)  
</br>

## Screen

#### User
</br>
<table algin="center">
   <tr>
      <td colspan="1" align="center"><img src="https://user-images.githubusercontent.com/74501631/229884791-e98b11d3-5c50-4689-adac-6dbf6465ea4e.png" width="250px"/></td>
      <td colspan="1" align="center"><img src="https://user-images.githubusercontent.com/74501631/229884896-8c18901c-87df-4c8c-a898-2040416b147f.png" width="250px"/></td>
  </table> 
  </br>
  
#### Caregiver
</br>
<table algin="center">
   <tr>
      <td colspan="1" align="center"><img src="https://user-images.githubusercontent.com/74501631/229883021-ceaebd72-4f9f-49aa-b0dd-d0ca4e9562f0.png" width="250px"/></td>
      <td colspan="1" align="center"><img src="https://user-images.githubusercontent.com/74501631/229884158-ad5cd93a-f248-4865-be30-d0e9c4cb4846.png" width="250px"/></td>
      <td colspan="1" align="center"><img src="https://user-images.githubusercontent.com/74501631/229884235-4f55f7b6-0a30-4978-a30b-c9f7da3fd9e4.png" width="250px"/></td>
      <td colspan="1" align="center"><img src="https://user-images.githubusercontent.com/74501631/229884329-7453c913-1f22-4dc0-8445-8df9cad05436.png" width="250px"/></td>
   </tr>
  </table> 

## Repository
[Android](https://github.com/GDSC-seeNear/FE) - Kotlin  
[BackEnd](https://github.com/GDSC-seeNear/BE) - Java, Spring Boot    
[AI-Chatbot](https://github.com/GDSC-seeNear/AI_chatbot) - Pytorch, Huggingface, Tensorflow (lite)  
[AI-Named Entity Recognition](https://github.com/GDSC-seeNear/NER) - Pytorch, Huggingface  
[AI-Sentiment Analysis](https://github.com/GDSC-seeNear/sentiment_repository) - Pytorch  
</br>


## Team Member  

<table algin="center">
   <tr>
      <td colspan="1" align="center"><strong>Android</strong></td>
      <td colspan="1" align="center"><strong>Back-End</strong></td>
      <td colspan="2" align="center"><strong>DL/AI</strong></td>
   </tr>
  <tr>
     <td align="center">
        <a href="https://github.com/gaguriee"><img src="https://avatars.githubusercontent.com/u/74501631?v=4" width="150px" alt="김가경"/><br /><sub><b>김가경</b></sub></a>
     </td>
    <td align="center">
    <a href="https://github.com/happyjamy"><img src="https://avatars.githubusercontent.com/u/78072370?v=4" width="150px;" alt="김주환"/><br /><sub><b>김주환</b></sub></a><br />
    </td>
     <td align="center">
        <a href="https://github.com/hyeok55"><img src="https://avatars.githubusercontent.com/u/67605795?v=4" width="150px" alt="김혁"/><br /><sub><b>김혁</b></sub></a>
     </td>
     <td align="center">
        <a href="https://github.com/keonju2"><img src="https://avatars.githubusercontent.com/u/54880474?v=4" width="150px" alt="나건주"/><br /><sub><b>나건주</b></sub></a>
  <tr>
</table> 

### download
[Download APK](https://github.com/GDSC-seeNear/FE/releases/tag/v1.0.0)
