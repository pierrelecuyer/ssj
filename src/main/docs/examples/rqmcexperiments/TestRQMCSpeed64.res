Speed tests and comparisons for RQMC point sets with 64-bit implementation.
See the program TestRQMCSpeed64.java for the details. 

Speed test for n = 1048576, s = 10, and m = 10 replications.
------------------------------------------------------- 

Korobov lattice with random shift, implemented as an LCG 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.171875 seconds
Average = 0.4999995231628418

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.453125 seconds
Average = 0.5000000050408162

------------------------------------------------------- 

Rank-1 lattice with random shift 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.75 seconds
Average = 0.5000000050411335

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.734375 seconds
Average = 0.5000000050411335

------------------------------------------------------- 

Rank-1 lattice with random shift, cached 

Time to randomize the points only: 
Total CPU time:      0.71875 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.96875 seconds
Average = 0.5000000050411335

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.140625 seconds
Average = 0.5000000050411335

------------------------------------------------------- 

Rank-1 lattice with random shift + tent transformation 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.890625 seconds
Average = 0.5000000000069634

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.203125 seconds
Average = 0.5000000000069634

------------------------------------------------------- 

Sobol points with no randomization 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.25 seconds
Average = 0.4999995231628418

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.609375 seconds
Average = 0.4999995231628418

------------------------------------------------------- 

Sobol points with random digital shift only 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.234375 seconds
Average = 0.5000000050408144

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.546875 seconds
Average = 0.5000000050408144

------------------------------------------------------- 

Sobol points with random digital shift + tent transformation 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.6875 seconds
Average = 0.5

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.453125 seconds
Average = 0.5

------------------------------------------------------- 

Sobol points with LMS + RDS 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.234375 seconds
Average = 0.49999999999999795

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.5625 seconds
Average = 0.49999999999999795

------------------------------------------------------- 

Sobol points with LMS + RDS, cached 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      0.234375 seconds
Average = 0.4999995231628418

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      0.5 seconds
Average = 0.4999995231628418

------------------------------------------------------- 

Sobol points with LMS + RDS + indep random bits after k 

Time to randomize the points only: 
Total CPU time:      0.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      2.546875 seconds
Average = 0.4999999999754975

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      2.859375 seconds
Average = 0.5000000000133265

------------------------------------------------------- 

Sobol points with NUS 

Time to randomize the points only: 
Total CPU time:      27.265625 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      27.34375 seconds
Average = 0.49999999998939465

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      26.65625 seconds
Average = 0.49999999998939465

------------------------------------------------------- 

Independent random points cached 

Time to randomize the points only: 
Total CPU time:      1.0 seconds
Number of points (test) = 1048576.0

Time to randomize, generate with nextPoint(), and add the points coordinates: 
Total CPU time:      1.234375 seconds
Average = 0.5000558501953665

Time to randomize, generate with nextDouble(), and add the points coordinates: 
Total CPU time:      1.796875 seconds
Average = 0.5000558501953665

------------------------------------------------------- 

