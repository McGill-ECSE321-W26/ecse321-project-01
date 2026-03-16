## Meeting Minutes 14/03/2026

- **Meeting Type:** Standup
- **Date:** 14/03/2026
- **Time and place:** 7 PM, online
- **Project:** [ECSE 321 Project - Group 1](https://github.com/orgs/McGill-ECSE321-W26/projects/30)
- **Milestone:** Iteration 2
- **Note-taker:** @santipadron
- **Attendees:** @B-Baki, @Gnardisitor, @santipadron, @MinhVo2005, @jayjay4387, @tran-ethan, @Tangdavid1
- **Absent:** None

### Agenda

1. check on progress
2. help each other on stuff

### Discussion and notes

- read this: https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design
- **NO VERBS in API endpoints, only NOUNS**
- when you have a POST method that creates a resource, you should return http status code 201 CREATED, not 200
- Dragos you have no endpoints for DELETE (unless we decided that the manager just can't delete orders i forgot). if/When you add them, deletions standard for success are 204 NO CONTENT
- you should import from `org.springframework.transaction.annotation.Transactional` not the jakarta one. I know the tutorial uses the jakarta one which is kind of odd because it's outdated but wtvr
- Multiple https verbs can be mapped to the same endpoint (this is actually good practice which you should definitely do whenever you can), for example creating an order, modifying it and deleting one should map to the same endpoint but different https verbs. just make sure you don't have same http verbs that map to the same endpoint
- Filtering should never done via path variables, always query paremeter filter. In your case dragos specifically, you apply filtering via path variables. this is no bueno. specifically, `GET /api/order/customer/{customerID}` and `GET /api/order/status/{orderStatus}` . In standard rest design you would apply query parameter filter, for example `GET /api/orders?customerId={customerID}` (lmk if you need help w this)
- in general for collections of items we would use plural. i would also suggest dragos rename the path to `/orders` 
- when you have a path mapping that maps to the root of the Controller's api path, for example say you want to have a method which `/api/person` instead your controller method a GET mapping would be `@GetMapping` not `@GetMapping("")`
- You never need null checks when something returns a collection, it will always an empty collection by default
-  When you have a service method that does not modify data but only reads it, you should annotate with @Transactional(readOnly = true)
- you guys may need to utilize PATCH instead of PUT in some places. Most notably, when an endpoint is meant to do a partial modification of a resource and not a full one, you use PATCH not PUT (PUT is reserved exclusively for full resource modifications). For example, in my clothing controller i have a method which modifies specifically the stock quantity of a variant. In this case, the request json format should only take 1 value, the quantity, and does not modify any of the other attributes (like color or size). In this case, it should be PATCH not PUT
- also for consistency sakes in the path annotations always begin with a /, just cause this how it will be done usually in industry

### Decisions

- need to keep our code similar cause it's all over the place right now
- let's not forget documentation and good practices, even though we are close to the deadline

### Action items

| Action               | Owner     | Due Date        | GitHub Link |
| -------------------- | --------- | --------------- | ----------- |
| do your tasks        | @everyone | mar 15 (ASAP)   |             |
| documentation + wiki | @everyone | prioritize code |

### Blockers

- Santiago has trouble with his cart implementation, but help from ethan
- We all gotta manage the Impact Paper 2 due the same day for another class

### Other
